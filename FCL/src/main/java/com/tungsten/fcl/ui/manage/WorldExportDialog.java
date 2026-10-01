package com.tungsten.fcl.ui.manage;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fcl.ui.TaskDialog;
import com.tungsten.fcl.ui.UIManager;
import com.tungsten.fcl.util.TaskCancellationAction;
import com.tungsten.fclcore.game.World;
import com.tungsten.fclcore.task.Schedulers;
import com.tungsten.fclcore.task.Task;
import com.tungsten.fclcore.task.TaskExecutor;
import com.tungsten.fclcore.task.TaskListener;
import com.tungsten.fclcore.util.StringUtils;
import com.tungsten.fclcore.util.platform.OperatingSystem;
import com.tungsten.fcllibrary.component.dialog.FCLAlertDialog;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.io.File;
import java.nio.file.Paths;

public class WorldExportDialog extends FCLDialog {

    private final World world;
    private final String parent;

    private final WorldExportDialogState state;

    public WorldExportDialog(@NonNull Context context, World world, String parent) {
        super(context);
        this.world = world;
        this.parent = parent;
        setCancelable(false);
        state = new WorldExportDialogState(world.getWorldName() + ".zip", world.getWorldName());
        ComposeView composeView = WorldExportDialogCompose.createView(context, parent, state, this::export, this::dismiss);
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void export() {
        String fileName = state.getFileName();
        String name = state.getName();
        if (name.isEmpty() || StringUtils.isBlank(fileName) || !OperatingSystem.isNameValid(fileName) || new File(parent, fileName).exists()) {
            return;
        }
            TaskDialog taskDialog = new TaskDialog(getContext(), new TaskCancellationAction(AppCompatDialog::dismiss));
            taskDialog.setTitle(getContext().getString(R.string.message_doing));

            Task<?> task = Task.runAsync(getContext().getString(R.string.world_export_wizard, name), () -> world.export(Paths.get(new File(parent, fileName).getAbsolutePath()), name));
            TaskExecutor executor = task.executor(new TaskListener() {
                @Override
                public void onStop(boolean success, TaskExecutor executor) {
                    Schedulers.androidUIThread().execute(() -> {
                        if (success) {
                            FCLAlertDialog.Builder builder1 = new FCLAlertDialog.Builder(getContext());
                            builder1.setAlertLevel(FCLAlertDialog.AlertLevel.INFO);
                            builder1.setCancelable(false);
                            builder1.setMessage(getContext().getString(R.string.message_success));
                            builder1.setNegativeButton(getContext().getString(com.tungsten.fcl.R.string.dialog_positive), () -> UIManager.getInstance().getManageUI().dismissAllTempPages());
                            builder1.create().show();
                        } else {
                            if (executor.getException() == null)
                                return;
                            String appendix = StringUtils.getStackTrace(executor.getException());
                            FCLAlertDialog.Builder builder1 = new FCLAlertDialog.Builder(getContext());
                            builder1.setAlertLevel(FCLAlertDialog.AlertLevel.ALERT);
                            builder1.setCancelable(false);
                            builder1.setTitle(getContext().getString(R.string.message_failed));
                            builder1.setMessage(appendix);
                            builder1.setNegativeButton(getContext().getString(com.tungsten.fcl.R.string.dialog_positive), null);
                            builder1.create().show();
                        }
                    });
                }
            });
            taskDialog.setExecutor(executor);
            taskDialog.show();
            executor.start();
            dismiss();
    }
}
