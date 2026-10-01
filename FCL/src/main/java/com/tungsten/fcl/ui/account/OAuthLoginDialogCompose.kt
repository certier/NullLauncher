package com.tungsten.fcl.ui.account

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class OAuthLoginDialogState {
    var loggingIn by mutableStateOf(false)
        private set
    var progressVisible by mutableStateOf(false)
        private set

    fun setLoggingIn(value: Boolean) {
        loggingIn = value
    }

    fun setProgressVisible(value: Boolean) {
        progressVisible = value
    }
}

data class OAuthLoginDialogViews(val root: ComposeView, val progressText: TextView, val state: OAuthLoginDialogState)

object OAuthLoginDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        onLogin: Runnable,
        onCancel: Runnable,
        onLongPress: Runnable
    ): OAuthLoginDialogViews {
        val state = OAuthLoginDialogState()
        val progressText = TextView(context)
        val root = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    OAuthLoginContent(state, progressText, onLogin, onCancel, onLongPress)
                }
            }
        }
        return OAuthLoginDialogViews(root, progressText, state)
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun OAuthLoginContent(
    state: OAuthLoginDialogState,
    progressText: TextView,
    onLogin: Runnable,
    onCancel: Runnable,
    onLongPress: Runnable
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(context.getString(R.string.account_login_refresh), style = MaterialTheme.typography.titleMedium)
        Text(context.getString(R.string.account_login_refresh_microsoft_hint), style = MaterialTheme.typography.bodyMedium)
        Text(context.getString(R.string.account_login_external_browser_hint), style = MaterialTheme.typography.bodyMedium)
        if (state.progressVisible) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircularProgressIndicator()
                AndroidView(factory = { progressText }, modifier = Modifier.weight(1f))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
                    .background(if (state.loggingIn) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary)
                    .combinedClickable(
                        enabled = !state.loggingIn,
                        onClick = onLogin::run,
                        onLongClick = onLongPress::run
                    )
                    .semantics { role = Role.Button }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.account_login),
                    color = if (state.loggingIn) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                )
            }
            TextButton(onClick = onCancel::run, enabled = !state.loggingIn) {
                Text(context.getString(R.string.dialog_negative))
            }
        }
    }
}
