package com.tungsten.fcl.ui.version;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fclcore.util.FutureCallback;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.util.concurrent.CompletableFuture;

public class RenameVersionDialog extends FCLDialog {

    private final FutureCallback<String> callback;
    private final CompletableFuture<String> future = new CompletableFuture<>();

    private final RenameVersionDialogState state;

    public RenameVersionDialog(@NonNull Context context, String oldName, FutureCallback<String> callback) {
        super(context);
        setCancelable(false);
        this.callback = callback;
        state = new RenameVersionDialogState(oldName);
        ComposeView composeView = RenameVersionDialogCompose.createView(context, state, this::onSubmit, this::dismiss);
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void onSubmit(String newName) {
        state.setEnabled(false);
        callback.call(newName, () -> {
            future.complete(newName);
            dismiss();
        }, msg -> {
            state.setEnabled(true);
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    public CompletableFuture<String> getFuture() {
        return future;
    }
}
