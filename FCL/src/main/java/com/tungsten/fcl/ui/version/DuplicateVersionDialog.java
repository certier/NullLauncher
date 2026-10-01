package com.tungsten.fcl.ui.version;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fcl.game.FCLGameRepository;
import com.tungsten.fcl.setting.Profile;
import com.tungsten.fclcore.util.FutureCallback;
import com.tungsten.fclcore.util.StringUtils;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.util.ArrayList;

public class DuplicateVersionDialog extends FCLDialog {

    private final Profile profile;
    private final String version;
    private final FutureCallback<ArrayList<Object>> callback;

    private final DuplicateVersionDialogState state;

    public DuplicateVersionDialog(@NonNull Context context, Profile profile, String version, FutureCallback<ArrayList<Object>> callback) {
        super(context);
        this.profile = profile;
        this.version = version;
        this.callback = callback;
        setCancelable(false);
        state = new DuplicateVersionDialogState(version);
        ComposeView composeView = DuplicateVersionDialogCompose.createView(context, state, this::onSubmit, this::dismiss);
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (430 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void onSubmit() {
        String newVersionName = state.getName();
        if (StringUtils.isBlank(newVersionName)) {
            Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
        } else if (profile.getRepository().versionIdConflicts(newVersionName)) {
            Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_already_exists), Toast.LENGTH_SHORT).show();
        } else if (!FCLGameRepository.isValidVersionId(newVersionName)) {
            Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
        } else {
            state.setEnabled(false);
            ArrayList<Object> res = new ArrayList<>();
            res.add(newVersionName);
            res.add(state.getDuplicateSave());
            callback.call(res, () -> {
                state.setEnabled(true);
                dismiss();
            }, msg -> {
                state.setEnabled(true);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
