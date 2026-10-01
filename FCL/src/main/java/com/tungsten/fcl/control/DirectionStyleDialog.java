package com.tungsten.fcl.control;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fcl.control.data.ControlDirectionStyle;
import com.tungsten.fcl.control.data.ControlViewGroup;
import com.tungsten.fcl.control.data.DirectionStyles;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

public class DirectionStyleDialog extends FCLDialog implements DirectionStyleDialogActions {

    private final boolean select;
    private final ControlDirectionStyle initStyle;
    private final Callback callback;

    private GameMenu menu;
    private final DirectionStyleDialogState state;

    public interface Callback {
        void onStyleSelect(ControlDirectionStyle style);
    }

    public DirectionStyleDialog(@NonNull Context context, boolean select, @Nullable ControlDirectionStyle initStyle, Callback callback) {
        super(context);
        this.select = select;
        this.initStyle = initStyle;
        this.callback = callback;
        setCancelable(false);
        if (getWindow() != null) getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        state = new DirectionStyleDialogState(DirectionStyles.getStyles(), initStyle);
        setContentView(DirectionStyleDialogCompose.createView(context, state, select, this),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void refreshList() {
        state.refresh(DirectionStyles.getStyles());
    }

    @Override
    public void onAddStyle() {
        AddDirectionStyleDialog dialog = new AddDirectionStyleDialog(getContext(), null, false, style -> {
            DirectionStyles.addStyle(style);
            refreshList();
        });
        dialog.show();
    }

    @Override
    public void onEditStyle() {
        ControlDirectionStyle before = state.getSelectedStyle();
        if (before == null) return;
        AddDirectionStyleDialog dialog = new AddDirectionStyleDialog(getContext(), before, true, style -> {
            int index = DirectionStyles.getStyles().indexOf(before);
            String beforeName = before.getName();
            DirectionStyles.removeStyles(before);
            DirectionStyles.addStyle(style, index);
            refreshList();
            state.setSelectedStyle(style);
            if (menu != null) {
                ControlViewGroup viewGroup = menu.getViewGroup();
                if (viewGroup != null) {
                    viewGroup.getViewData().directionList().forEach(it -> {
                        String name = it.getStyle().getName();
                        if (name.equals(style.getName()) || name.equals(beforeName)) it.setStyle(style);
                    });
                }
            }
        });
        dialog.setGameMenu(menu);
        dialog.show();
    }

    @Override
    public void onDeleteStyle(ControlDirectionStyle style) {
        FCLAlertDialog.Builder builder = new FCLAlertDialog.Builder(getContext());
        builder.setCancelable(false).setAlertLevel(FCLAlertDialog.AlertLevel.INFO)
                .setMessage(getContext().getString(R.string.style_warning_delete))
                .setPositiveButton(() -> {
                    DirectionStyles.removeStyles(style);
                    DirectionStyles.checkStyles();
                    refreshList();
                }).setNegativeButton(null).create().show();
    }

    @Override
    public void onSelectStyle(ControlDirectionStyle style) {
        state.setSelectedStyle(style);
    }

    @Override
    public void onConfirm() {
        ControlDirectionStyle selected = state.getSelectedStyle();
        dismiss();
        if (callback != null && select && selected != null) {
            callback.onStyleSelect(selected);
        }
    }

    public void setGameMenu(GameMenu menu) {
        this.menu = menu;
    }
}
