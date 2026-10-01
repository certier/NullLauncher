package com.tungsten.fcl.ui.manage;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fclcore.mod.LocalModFile;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.util.List;

public class ModRollbackDialog extends FCLDialog {

    public ModRollbackDialog(@NonNull Context context, List<LocalModFile> list, Callback callback) {
        super(context);
        setCancelable(false);
        setContentView(ModRollbackDialogCompose.createView(context, list, localModFile -> {
            dismiss();
            callback.onOldVersionSelect(localModFile);
        }, this::dismiss), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setLayout((int) (300 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public interface Callback {
        void onOldVersionSelect(LocalModFile localModFile);
    }
}
