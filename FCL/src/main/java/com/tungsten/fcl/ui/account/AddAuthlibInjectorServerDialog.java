package com.tungsten.fcl.ui.account;

import static com.tungsten.fcl.setting.ConfigHolder.config;
import static com.tungsten.fclcore.util.Logging.LOG;

import android.content.Context;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;

import com.tungsten.fcl.R;
import com.tungsten.fclcore.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.fclcore.task.Schedulers;
import com.tungsten.fclcore.task.Task;
import com.tungsten.fcllibrary.component.dialog.FCLDialog;

import java.io.IOException;
import java.util.logging.Level;

import javax.net.ssl.SSLException;

public class AddAuthlibInjectorServerDialog extends FCLDialog {

    private final AddAuthlibServerDialogState state = new AddAuthlibServerDialogState();

    private AuthlibInjectorServer serverBeingAdded;

    public AddAuthlibInjectorServerDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
        ComposeView composeView = AddAuthlibServerDialogCompose.createView(
                context,
                state,
                this::next,
                this::back,
                this::saveServer,
                this::dismiss
        );
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setLayout((int) (400 * context.getResources().getDisplayMetrics().density), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void next() {
        state.setLoading(true);
        String url = state.getUrl();
        Task.runAsync(() -> {
            serverBeingAdded = AuthlibInjectorServer.locateServer(url);
        }).whenComplete(Schedulers.androidUIThread(), exception -> {
            state.setLoading(false);

            if (exception == null) {
                state.setResolvedServer(serverBeingAdded.getUrl(), serverBeingAdded.getName());
            } else {
                LOG.log(Level.WARNING, "Failed to resolve auth server: " + url, exception);
                Toast.makeText(getContext(), resolveFetchExceptionMessage(exception), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private String resolveFetchExceptionMessage(Throwable exception) {
        if (exception instanceof SSLException) {
            return getContext().getString(R.string.account_failed_ssl);
        } else if (exception instanceof IOException) {
            return getContext().getString(R.string.account_failed_connect_injector_server);
        } else {
            return exception.getClass().getName() + ": " + exception.getLocalizedMessage();
        }
    }

    private void back() {
        state.showUrlEntry();
    }

    private void saveServer() {
        if (!config().getAuthlibInjectorServers().contains(serverBeingAdded)) {
            config().getAuthlibInjectorServers().add(serverBeingAdded);
        }
        dismiss();
    }
}
