package com.tungsten.fcl.ui.account

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.tungsten.fcl.R
import com.tungsten.fcl.setting.Accounts
import com.tungsten.fclcore.auth.AuthInfo
import com.tungsten.fclcore.auth.ClassicAccount
import com.tungsten.fclcore.task.Schedulers
import com.tungsten.fclcore.task.Task
import com.tungsten.fclcore.util.Logging.LOG
import com.tungsten.fcllibrary.component.dialog.FCLAlertDialog
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import java.util.function.Consumer
import java.util.logging.Level

/** Classic 账户（Yggdrasil / authlib-injector）凭据过期后的密码重登弹窗 */
class ClassicAccountLoginDialog(
    context: Context,
    private val account: ClassicAccount,
    private val success: Consumer<AuthInfo>,
    private val failed: Runnable,
) : FCLDialog(context) {

    private val state = ClassicAccountLoginState()

    init {
        setCancelable(false)
        window?.setLayout((400 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(ClassicAccountLoginCompose.createView(
            context,
            account.username,
            state,
            ::logIn,
            {
                failed.run()
                dismiss()
            }
        ), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun logIn(password: String) {
        if (password.isEmpty()) {
            state.setError(context.getString(R.string.input_hint_not_empty))
            return
        }
        state.setEnabled(false)
        state.setError(null)
        Task.supplyAsync { account.logInWithPassword(password) }
            .whenComplete(Schedulers.androidUIThread()) { authInfo, exception ->
                if (exception == null) {
                    success.accept(authInfo)
                    dismiss()
                } else {
                    LOG.log(Level.INFO, "Failed to login with password: $account", exception)
                    FCLAlertDialog.Builder(context).apply {
                        setAlertLevel(FCLAlertDialog.AlertLevel.ALERT)
                        setMessage(Accounts.localizeErrorMessage(context, exception))
                        setCancelable(false)
                        setNegativeButton(context.getString(R.string.dialog_positive), null)
                    }.create().show()
                    state.setEnabled(true)
                }
            }.start()
    }
}
