package com.tungsten.fcllibrary.crash;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.tungsten.fcl.R;
import com.tungsten.fcllibrary.component.FCLActivity;
import com.tungsten.fcllibrary.component.theme.ThemeEngine;
import com.tungsten.fcllibrary.util.LogSharingUtilsKt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class CrashReportActivity extends FCLActivity {

    private CrashReporterConfig config;
    private View root;
    private String errorDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getSharedPreferences("launcher", MODE_PRIVATE).getBoolean("allowScreenshots", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        config = CrashReporter.getConfigFromIntent(getIntent());

        if (config == null) {
            finish();
            return;
        }

        errorDetails = CrashReporter.getAllErrorDetailsFromIntent(this, getIntent());
        root = CrashReportCompose.createView(
                this,
                errorDetails,
                this::restartApplication,
                this::closeApplication,
                this::uploadLog,
                this::shareLog
        );
        setContentView(root);
        ThemeEngine.getInstance().registerEvent(root, this::applyWindowBackground);
    }

    /** 解析当前生效主题的窗口背景色并设置到根布局，与主界面背景保持一致 */
    private void applyWindowBackground() {
        TypedValue outValue = new TypedValue();
        int color;
        if (getTheme().resolveAttribute(android.R.attr.windowBackground, outValue, true)
                && outValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && outValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            color = outValue.data;
        } else {
            // windowBackground 非纯色时退回按亮暗取黑白
            color = ThemeEngine.getInstance().isNightMode(this) ? Color.BLACK : Color.WHITE;
        }
        root.setBackgroundColor(color);
    }

    private void restartApplication() {
        CrashReporter.restartApplication(this, config);
    }

    private void closeApplication() {
        CrashReporter.closeApplication(this, config);
    }

    private void uploadLog() {
        LogSharingUtilsKt.uploadLog(this, errorDetails);
    }

    private void shareLog() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            File file = File.createTempFile("crash_report", ".txt");
            Files.write(file.toPath(), errorDetails.getBytes(StandardCharsets.UTF_8));
            Uri uri = FileProvider.getUriForFile(this, getApplication().getPackageName() + ".provider", file);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, getString(R.string.crash_reporter_share)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
