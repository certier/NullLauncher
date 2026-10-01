package com.tungsten.fcl.ui.version;

import android.content.Context;
import com.tungsten.fcl.ui.UIManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fcl.activity.MainActivity;
import com.tungsten.fcl.setting.Profile;
import com.tungsten.fcl.setting.Profiles;
import com.tungsten.fclcore.util.StringUtils;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.io.File;

public class AddProfileDialog extends FCLDialog {

    private final AddProfileDialogState state;

    public AddProfileDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
        state = new AddProfileDialogState();
        ComposeView composeView = AddProfileDialogCompose.createView(
                context,
                state,
                this::choosePath,
                this::createProfile,
                this::dismiss
        );
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void choosePath() {
        MainActivity.getInstance().fileLauncher.launchSingleSelection(null, null, true, files -> {
            if (files != null) state.setPath(files.get(0).getPath());
        });
    }

    private void createProfile() {
        String name = state.getName();
        String path = state.getPath();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(path)) {
            Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
        } else if (Profiles.getProfiles().stream().anyMatch(profile -> profile.getName().equals(name))) {
            Toast.makeText(getContext(), getContext().getString(R.string.profile_already_exist), Toast.LENGTH_SHORT).show();
        } else {
            Profiles.addProfile(new Profile(name, new File(path)));
            ((VersionListPage) UIManager.getInstance().getVersionUI().getPage(0)).refreshProfile();
            dismiss();
        }
    }
}
