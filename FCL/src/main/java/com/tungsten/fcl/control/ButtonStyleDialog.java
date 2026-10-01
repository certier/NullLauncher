package com.tungsten.fcl.control;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fcl.control.data.ButtonStyles;
import com.tungsten.fcl.control.data.ControlButtonStyle;
import com.tungsten.fcl.control.data.ControlViewGroup;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

public class ButtonStyleDialog extends FCLDialog implements ButtonStyleDialogActions {

    private final boolean select;
    private final ControlButtonStyle initStyle;
    private final Callback callback;

    private GameMenu menu;
    private final ButtonStyleDialogState state;

    public interface Callback {
        void onStyleSelect(ControlButtonStyle style);
    }

    public ButtonStyleDialog(@NonNull Context context, boolean select, @Nullable ControlButtonStyle initStyle, Callback callback) {
        super(context);
        this.select = select;
        this.initStyle = initStyle;
        this.callback = callback;
        setCancelable(false);
        if (getWindow() != null) getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT);
        state = new ButtonStyleDialogState(ButtonStyles.getStyles(), initStyle);
        setContentView(ButtonStyleDialogCompose.createView(context, state, select, this),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void refreshList() {
        state.refresh(ButtonStyles.getStyles());
    }

    @Override
    public void onAddStyle() {
        AddButtonStyleDialog dialog = new AddButtonStyleDialog(getContext(), null, false, style -> {
            ButtonStyles.addStyle(style);
            refreshList();
        });
        dialog.show();
    }

    @Override
    public void onEditStyle() {
        ControlButtonStyle before = state.getSelectedStyle();
        if (before == null) return;
        AddButtonStyleDialog dialog = new AddButtonStyleDialog(getContext(), before, true, style -> {
            int index = ButtonStyles.getStyles().indexOf(before);
            String beforeName = before.getName();
            ButtonStyles.removeStyles(before);
            ButtonStyles.addStyle(style, index);
            refreshList();
            state.setSelectedStyle(style);
            if (menu != null) {
                ControlViewGroup viewGroup = menu.getViewGroup();
                if (viewGroup != null) {
                    viewGroup.getViewData().buttonList().forEach(it -> {
                        String name = it.getStyle().getName();
                        if (name.equals(style.getName()) || name.equals(beforeName)) it.setStyle(style);
                    });
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onDeleteStyle(ControlButtonStyle style) {
        FCLAlertDialog.Builder builder = new FCLAlertDialog.Builder(getContext());
        builder.setCancelable(false).setAlertLevel(FCLAlertDialog.AlertLevel.INFO)
                .setMessage(getContext().getString(R.string.style_warning_delete))
                .setPositiveButton(() -> {
                    ButtonStyles.removeStyles(style);
                    ButtonStyles.checkStyles();
                    refreshList();
                }).setNegativeButton(null).create().show();
    }

    @Override
    public void onSelectStyle(ControlButtonStyle style) {
        state.setSelectedStyle(style);
    }

    @Override
    public void onConfirm() {
        ControlButtonStyle selected = state.getSelectedStyle();
        dismiss();
        if (callback != null && select && selected != null) {
            callback.onStyleSelect(selected);
        }
    }

    public void setGameMenu(GameMenu menu) {
        this.menu = menu;
    }
}
