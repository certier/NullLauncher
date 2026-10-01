package com.tungsten.fcl.ui.download.modpack;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcllibrary.component.dialog.FCLDialog;

public class ModpackUrlDialog extends FCLDialog {

    public interface Callback {
        void onPositive(String urlString);
    }

    public ModpackUrlDialog(@NonNull Context context, Callback callback) {
        super(context);
        setCancelable(false);
        setContentView(ModpackUrlDialogCompose.createView(
            context,
            url -> {
                callback.onPositive(url);
                dismiss();
            },
            this::dismiss
        ), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
