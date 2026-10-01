package com.tungsten.fcl.ui.manage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.mio.util.AndroidUtilKt;
import com.tungsten.fclcore.mod.ModLoaderType;
import com.tungsten.fclcore.task.Schedulers;
import com.tungsten.fclcore.task.Task;
import com.tungsten.fclcore.util.StringUtils;
import com.tungsten.fclcore.util.io.CompressingUtils;
import com.tungsten.fclcore.util.io.FileUtils;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModInfoDialog extends FCLDialog {

    private final ModListPage.ModInfoObject modInfoObject;
    private final ModInfoDialogState state;

    @SuppressLint("UseCompatLoadingForDrawables")
    public ModInfoDialog(@NonNull Context context, ModListPage.ModInfoObject modInfoObject) {
        super(context);
        this.modInfoObject = modInfoObject;
        setCancelable(false);
        state = new ModInfoDialogState(
            modInfoObject.getModInfo().getName(),
            getTag(modInfoObject),
            FileUtils.getName(modInfoObject.getModInfo().getFile()),
            modInfoObject.getModInfo().getDescription().toString(),
            modInfoObject.getModInfo().getUrl(),
            StringUtils.isNotBlank(modInfoObject.getModInfo().getUrl())
        );
        setContentView(ModInfoDialogCompose.createView(context, state, this::openWebsite, this::dismiss),
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density),
            (int) (240 * context.getResources().getDisplayMetrics().density));

        if (StringUtils.isNotBlank(modInfoObject.getModInfo().getLogoPath())) {
            Task.supplyAsync(() -> {
                try (FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(modInfoObject.getModInfo().getFile())) {
                    Path iconPath = fs.getPath(modInfoObject.getModInfo().getLogoPath());
                    if (Files.exists(iconPath)) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Files.copy(iconPath, stream);
                        return new ByteArrayInputStream(stream.toByteArray());
                    }
                }
                return null;
            }).whenComplete(Schedulers.androidUIThread(), (stream, exception) -> {
                if (stream != null) {
                    state.setLogo(BitmapFactory.decodeStream(stream));
                } else {
                    state.setLogo(null);
                }
            }).start();
        }
    }

    private void openWebsite() {
        if (StringUtils.isNotBlank(modInfoObject.getModInfo().getUrl())) {
            AndroidUtilKt.openLink(getContext(), modInfoObject.getModInfo().getUrl());
        }
    }

    private String getTag(ModListPage.ModInfoObject modInfoObject) {
        String modLoaderType = getModLoader(modInfoObject.getModInfo().getModLoaderType());
        String split = modLoaderType.equals("") ? "" : "   ";
        return modLoaderType + split + modInfoObject.getModInfo().getVersion();
    }

    private String getModLoader(ModLoaderType modLoaderType) {
        switch (modLoaderType) {
            case FORGE:
                return getContext().getString(R.string.install_installer_forge);
            case NEO_FORGED:
                return getContext().getString(R.string.install_installer_neoforge);
            case FABRIC:
                return getContext().getString(R.string.install_installer_fabric);
            case LITE_LOADER:
                return getContext().getString(R.string.install_installer_liteloader);
            case QUILT:
                return getContext().getString(R.string.install_installer_quilt);
            default:
                return "";
        }
    }
}
