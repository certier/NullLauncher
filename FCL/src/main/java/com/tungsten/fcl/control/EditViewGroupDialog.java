package com.tungsten.fcl.control;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fcl.control.data.ControlViewGroup;
import com.tungsten.fclcore.util.StringUtils;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.util.ArrayList;
import java.util.Objects;

public class EditViewGroupDialog extends FCLDialog {

    private final GameMenu menu;
    private final ControlViewGroup viewGroup;
    private final Callback callback;

    private final EditViewGroupDialogState state;

    public interface Callback {
        void onPositive(String name, ControlViewGroup.Visibility visibility);
    }

    public EditViewGroupDialog(@NonNull Context context, GameMenu menu, ControlViewGroup viewGroup, Callback callback) {
        super(context);
        this.menu = menu;
        this.viewGroup = viewGroup;
        this.callback = callback;
        setCancelable(false);
        state = new EditViewGroupDialogState(viewGroup.getName(),
                viewGroup.getVisibility() == ControlViewGroup.Visibility.VISIBLE);
        setContentView(EditViewGroupDialogCompose.createView(getContext(), state, this::submit, this::dismiss),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void submit() {
        String name = state.getName();
        if (menu.getController().viewGroups().stream().anyMatch(it -> it.getName().equals(name) && !viewGroup.getName().equals(name))) {
            Toast.makeText(getContext(), getContext().getString(R.string.menu_control_view_group_exist), Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isBlank(name)) {
            Toast.makeText(getContext(), getContext().getString(R.string.menu_control_view_group_empty), Toast.LENGTH_SHORT).show();
        } else {
            dismiss();
            callback.onPositive(name, state.getVisible() ? ControlViewGroup.Visibility.VISIBLE : ControlViewGroup.Visibility.INVISIBLE);
        }
    }
}
