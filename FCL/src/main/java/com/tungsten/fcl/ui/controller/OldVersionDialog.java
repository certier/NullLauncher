package com.tungsten.fcl.ui.controller;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.control.download.ControllerVersion;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.util.ArrayList;

public class OldVersionDialog extends FCLDialog {

    public OldVersionDialog(@NonNull Context context, ArrayList<ControllerVersion.VersionInfo> versionInfos, Callback callback) {
        super(context);
        setCancelable(false);
        setContentView(OldVersionDialogCompose.createView(context, versionInfos, versionInfo -> {
            callback.download(versionInfo.getVersionCode());
            dismiss();
        }, this::dismiss), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public interface Callback {
        void download(int versionCode);
    }
}
