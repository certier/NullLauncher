package com.tungsten.fcl.ui.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fcl.setting.Controller;
import com.tungsten.fclcore.util.StringUtils;
import com.tungsten.fclcore.util.platform.OperatingSystem;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;
import com.tungsten.fcllibrary.util.ConvertUtils;

public class ControllerInfoDialog extends FCLDialog {

    private final boolean create;
    private final Controller controller;
    private final Callback callback;

    private final ControllerInfoDialogState state;

    @SuppressLint("SetTextI18n")
    public ControllerInfoDialog(@NonNull Context context, boolean create, Controller controller, Callback callback) {
        super(context);
        this.create = create;
        this.controller = controller;
        this.callback = callback;
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ConvertUtils.dip2px(getContext(), 400), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        setCancelable(false);
        state = new ControllerInfoDialogState(
                controller.getName(),
                controller.getVersion(),
                String.valueOf(controller.getVersionCode()),
                controller.getAuthor(),
                controller.getDescription(),
                create ? getContext().getString(R.string.control_create) : getContext().getString(R.string.control_info_edit)
        );
        ComposeView composeView = ControllerInfoDialogCompose.createView(getContext(), state, this::submit, this::dismiss);
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void submit() {
            String name = state.getName();
            String version = state.getVersion();
            String versionCode = state.getVersionCode();
            String author = state.getAuthor();
            String description = state.getDescription();
            if (!OperatingSystem.isNameValid(name) || name.equals("Error")) {
                Toast.makeText(getContext(), getContext().getString(R.string.control_info_name_invalid), Toast.LENGTH_SHORT).show();
            } else {
                String id = this.controller.getId();
                if (!author.equals(this.controller.getAuthor())) {
                    id = Controller.generateRandomId();
                }
                Controller controller = new Controller(id,
                        name,
                        version,
                        Integer.parseInt(StringUtils.isBlank(versionCode) ? "1" : versionCode),
                        author,
                        description,
                        this.controller.getControllerVersion());
                callback.onInfoGenerate(controller);
                dismiss();
            }
    }

    public interface Callback {
        void onInfoGenerate(Controller controller);
    }
}
