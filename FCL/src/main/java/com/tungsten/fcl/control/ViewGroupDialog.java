package com.tungsten.fcl.control;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;
import androidx.appcompat.app.AppCompatDialog;

import com.tungsten.fcl.control.data.ControlViewGroup;
import com.tungsten.fclcore.fakefx.collections.ObservableList;
import com.tungsten.fcllibrary.component.dialog.FCLAlertDialog;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;
import com.tungsten.fcllibrary.util.ConvertUtils;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class ViewGroupDialog extends FCLDialog implements ViewGroupDialogActions {

    private final GameMenu gameMenu;
    private final boolean select;
    private final ObservableList<ControlViewGroup> selectedGroups;
    @Nullable
    private final Callback callback;
    private final ViewGroupDialogState state;

    public interface Callback {
        void onSelect(ObservableList<ControlViewGroup> viewGroup);
    }

    public ViewGroupDialog(@NonNull Context context, GameMenu gameMenu, boolean select, ObservableList<ControlViewGroup> selectedGroups, @Nullable Callback callback) {
        super(context);
        this.gameMenu = gameMenu;
        this.select = select;
        this.selectedGroups = selectedGroups;
        this.callback = callback;
        setCancelable(false);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = ConvertUtils.dip2px(context, 400);
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        state = new ViewGroupDialogState(gameMenu.getController().viewGroups(),
                selectedGroups.stream().map(ControlViewGroup::getId).collect(Collectors.toSet()));
        setContentView(ViewGroupDialogCompose.createView(context, select, state, this),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onSelectionChange(ControlViewGroup group, boolean checked) {
        if (checked) {
            if (selectedGroups.stream().noneMatch(item -> item.getId().equals(group.getId()))) {
                selectedGroups.add(group);
            }
        } else {
            selectedGroups.removeIf(item -> item.getId().equals(group.getId()));
        }
    }

    @Override
    public void onMove(int from, int to) {
        ObservableList<ControlViewGroup> groups = gameMenu.getController().viewGroups();
        if (from < 0 || to < 0 || from >= groups.size() || to >= groups.size()) return;
        Collections.swap(groups, from, to);
        gameMenu.getController().updateViewGroup(groups.get(to));
        state.move(from, to);
    }

    @Override
    public void onEdit(ControlViewGroup group) {
        EditViewGroupDialog dialog = new EditViewGroupDialog(getContext(), gameMenu, group, (name, visibility) -> {
            group.setName(name);
            group.setVisibility(visibility);
            gameMenu.getController().updateViewGroup(group);
            state.refresh(gameMenu.getController().viewGroups());
        });
        dialog.show();
    }

    @Override
    public void onDelete(ControlViewGroup group) {
        FCLAlertDialog.Builder builder = new FCLAlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setAlertLevel(FCLAlertDialog.AlertLevel.INFO);
        builder.setMessage(getContext().getString(com.tungsten.fcl.R.string.menu_control_view_group_delete));
        builder.setPositiveButton(() -> {
            gameMenu.getController().removeViewGroup(group);
            selectedGroups.removeIf(item -> item.getId().equals(group.getId()));
            state.refresh(gameMenu.getController().viewGroups());
        });
        builder.setNegativeButton(null);
        builder.create().show();
    }

    @Override
    public void onAdd() {
        EditViewGroupDialog dialog = new EditViewGroupDialog(getContext(), gameMenu,
                new ControlViewGroup(UUID.randomUUID().toString()), (name, visibility) -> {
            ControlViewGroup group = new ControlViewGroup(UUID.randomUUID().toString());
            group.setName(name);
            group.setVisibility(visibility);
            group.setDataLoaded(true);
            gameMenu.getController().addViewGroup(group);
            state.refresh(gameMenu.getController().viewGroups());
        });
        dialog.show();
    }

    @Override
    public void onConfirm() {
        if (callback != null) callback.onSelect(selectedGroups);
        dismiss();
    }

    @Override
    public void onDismiss() {
        dismiss();
    }
}
