package com.tungsten.fcl.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.compose.ui.platform.ComposeView;
import androidx.core.content.FileProvider;

import com.tungsten.fcl.R;
import com.tungsten.fcl.ui.TaskDialog;
import com.mio.util.AndroidUtilKt;
import com.tungsten.fcl.util.TaskCancellationAction;
import com.tungsten.fclauncher.bridge.FCLBridge;
import com.tungsten.fclauncher.utils.Architecture;
import com.tungsten.fclauncher.utils.FCLPath;
import com.tungsten.fclcore.task.FileDownloadTask;
import com.tungsten.fclcore.task.Schedulers;
import com.tungsten.fclcore.task.Task;
import com.tungsten.fclcore.task.TaskExecutor;
import com.tungsten.fclcore.util.io.NetworkUtils;
import com.tungsten.fcllibrary.component.dialog.FCLAlertDialog;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.io.File;
import java.util.concurrent.CancellationException;

public class UpdateDialog extends FCLDialog {

    private final RemoteVersion version;

    public UpdateDialog(@NonNull Context context, RemoteVersion version) {
        super(context);
        this.version = version;
        setCancelable(false);
        setContentView(UpdateDialogCompose.createView(
                context,
                version,
                this::ignoreUpdate,
                this::startUpdate,
                this::openNetdisk,
                this::dismiss,
                this::openLatestRelease
        ), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (450 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void ignoreUpdate() {
        UpdateChecker.setIgnore(getContext(), version.getVersionCode());
        dismiss();
    }

    private void startUpdate() {
            TaskDialog dialog = new TaskDialog(getContext(), new TaskCancellationAction(AppCompatDialog::dismiss));
            dialog.setTitle(getContext().getString(R.string.update_launcher));
            Schedulers.androidUIThread().execute(() -> {
                TaskExecutor executor = Task.composeAsync(() -> {
                    FileDownloadTask task = new FileDownloadTask(NetworkUtils.toURL(getTargetArchUrl()), new File(FCLPath.CACHE_DIR, "FoldCraftLauncher.apk"));
                    task.setName("FoldCraftLauncher");
                    return task.whenComplete(Schedulers.androidUIThread(), exception -> {
                        if (exception == null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri apkUri = FileProvider.getUriForFile(getContext(), getContext().getString(com.tungsten.fcl.R.string.file_browser_provider), new File(FCLPath.CACHE_DIR, "FoldCraftLauncher.apk"));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            getContext().startActivity(intent);
                        } else if (!(exception instanceof CancellationException)) {
                            FCLAlertDialog.Builder builder = new FCLAlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setAlertLevel(FCLAlertDialog.AlertLevel.ALERT);
                            builder.setMessage(getContext().getString(R.string.update_failed) + "\n" + exception.getMessage());
                            builder.setNegativeButton(getContext().getString(com.tungsten.fcl.R.string.dialog_positive), null);
                            builder.setPositiveButton(getContext().getString(R.string.update_netdisk), ()->{
                                AndroidUtilKt.openLink(getContext(), version.getNetdiskUrl());
                            });
                            builder.create().show();
                        }
                    });
                }).executor();
                dialog.setExecutor(executor);
                dialog.show();
                executor.start();
            });
        dismiss();
    }

    private void openNetdisk() {
        AndroidUtilKt.openLink(getContext(), version.getNetdiskUrl());
        dismiss();
    }

    private void openLatestRelease() {
        AndroidUtilKt.openLink(getContext(), "https://github.com/FCL-Team/FoldCraftLauncher/releases/latest");
    }

    @NonNull
    private String getTargetArchUrl() {
        String url = version.getUrl();
        String arch = switch (Architecture.getDeviceArchitecture()) {
            case Architecture.ARCH_ARM -> "armeabi-v7a";
            case Architecture.ARCH_ARM64 -> "arm64-v8a";
            case Architecture.ARCH_X86 -> "x86";
            case Architecture.ARCH_X86_64 -> "x86_64";
            default -> "all";
        };
        url = url.replace("-all", "-" + arch);
        return url;
    }
}
