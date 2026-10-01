package com.tungsten.fcl.ui.account;

import static com.tungsten.fclcore.util.Logging.LOG;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.game.OAuthServer;
import com.tungsten.fcl.setting.Accounts;
import com.mio.util.AndroidUtilKt;
import com.mio.util.LoginStageTextBinder;
import com.tungsten.fcl.util.FXUtils;
import com.tungsten.fclcore.auth.AuthInfo;
import com.tungsten.fclcore.auth.OAuthAccount;
import com.tungsten.fclcore.auth.microsoft.MicrosoftAccount;
import com.tungsten.fclcore.fakefx.beans.property.ObjectProperty;
import com.tungsten.fclcore.fakefx.beans.property.SimpleObjectProperty;
import com.tungsten.fclcore.task.Schedulers;
import com.tungsten.fclcore.task.Task;
import com.tungsten.fcllibrary.component.dialog.FCLAlertDialog;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import android.widget.TextView;

import java.util.function.Consumer;
import java.util.logging.Level;

public class OAuthAccountLoginDialog extends FCLDialog {

    /** 登录进度行（include view_login_progress），微软重登时显示当前阶段 */
    private final TextView progressText;
    private final OAuthLoginDialogState composeState;

    private final OAuthAccount account;
    private final Consumer<AuthInfo> success;
    private final Runnable failed;
    private final ObjectProperty<OAuthServer.GrantDeviceCodeEvent> deviceCode = new SimpleObjectProperty<>();

    // 强引用注册（register），dismiss 时注销，避免对话框关闭后残留监听重复打开登录页
    private final Consumer<OAuthServer.GrantDeviceCodeEvent> deviceCodeListener = deviceCode::set;
    private boolean useExternalBrowser = false;
    private final Consumer<OAuthServer.OpenBrowserEvent> openBrowserListener = event -> {
        if (useExternalBrowser) {
            AndroidUtilKt.openLink(getContext(), event.getUrl());
        } else {
            AndroidUtilKt.openLinkWithBuiltinWebView(getContext(), event.getUrl());
        }
    };

    public OAuthAccountLoginDialog(@NonNull Context context, OAuthAccount account, Consumer<AuthInfo> success, Runnable failed) {
        super(context);
        this.account = account;
        this.success = success;
        this.failed = failed;

        setCancelable(false);

        FXUtils.onChangeAndOperate(deviceCode, deviceCode -> Schedulers.androidUIThread().execute(() -> {
            if (deviceCode != null) {
                AndroidUtilKt.copyText(getContext(), deviceCode.getUserCode());
            }
        }));
        Accounts.OAUTH_CALLBACK.onGrantDeviceCode.register(deviceCodeListener);
        Accounts.OAUTH_CALLBACK.onOpenBrowser.register(openBrowserListener);

        OAuthLoginDialogViews views = OAuthLoginDialogCompose.createView(
            context,
            this::startLogin,
            this::cancelLogin,
            this::startExternalLogin
        );
        composeState = views.getState();
        progressText = views.getProgressText();
        setContentView(views.getRoot());
    }

    @Override
    public void dismiss() {
        Accounts.OAUTH_CALLBACK.onGrantDeviceCode.unregister(deviceCodeListener);
        Accounts.OAUTH_CALLBACK.onOpenBrowser.unregister(openBrowserListener);
        super.dismiss();
    }

    private void startExternalLogin() {
        useExternalBrowser = true;
        startLogin();
    }

    private void startLogin() {
        composeState.setLoggingIn(true);
        MicrosoftAccount microsoftAccount = account instanceof MicrosoftAccount ? (MicrosoftAccount) account : null;
        if (microsoftAccount != null) {
            composeState.setProgressVisible(true);
            progressText.setText(getContext().getString(com.tungsten.fcl.R.string.launch_state_logging_in));
            microsoftAccount.setProgressCallback(new LoginStageTextBinder(getContext(), progressText));
        }
        Task.supplyAsync(account::logInWhenCredentialsExpired)
                .whenComplete(Schedulers.androidUIThread(), (authInfo, exception) -> {
                    if (microsoftAccount != null) {
                        microsoftAccount.setProgressCallback(null);
                        composeState.setProgressVisible(false);
                    }
                    if (exception == null) {
                        success.accept(authInfo);
                        dismiss();
                    } else {
                        LOG.log(Level.INFO, "Failed to login when credentials expired: " + account, exception);
                        FCLAlertDialog.Builder builder = new FCLAlertDialog.Builder(getContext());
                        builder.setAlertLevel(FCLAlertDialog.AlertLevel.ALERT);
                        builder.setMessage(Accounts.localizeErrorMessage(getContext(), exception));
                        builder.setCancelable(false);
                        builder.setNegativeButton(getContext().getString(com.tungsten.fcl.R.string.dialog_positive), null);
                        builder.create().show();
                    }
                    composeState.setLoggingIn(false);
                    Accounts.OAUTH_CALLBACK.onLoginFinished.fireEvent(new OAuthServer.LoginFinishedEvent(this));
                }).start();
    }

    private void cancelLogin() {
        Accounts.OAUTH_CALLBACK.onLoginFinished.fireEvent(new OAuthServer.LoginFinishedEvent(this));
        failed.run();
        dismiss();
    }
}
