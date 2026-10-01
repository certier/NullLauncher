package com.tungsten.fcl.ui.download.common;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fclcore.util.platform.OperatingSystem;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

public class DownloadAddonDialog extends FCLDialog {

    private final Callback callback;
    private final DownloadAddonDialogState state;

    public DownloadAddonDialog(@NonNull Context context, String name, Callback callback) {
        super(context);
        this.callback = callback;
        setCancelable(false);
        state = new DownloadAddonDialogState(name);
        setContentView(DownloadAddonDialogCompose.createView(context, state, this::submit, this::dismiss),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void submit() {
        if (!OperatingSystem.isNameValid(state.getName())) {
            Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
        } else {
            callback.onPositive(state.getName());
            dismiss();
        }
    }

    public interface Callback {
        void onPositive(String name);
    }
}
