package com.tungsten.fcl.ui.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class ClassicAccountLoginState {
    var enabled by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun setEnabled(value: Boolean) {
        enabled = value
    }

    fun setError(value: String?) {
        error = value
    }
}

object ClassicAccountLoginCompose {
    @JvmStatic
    fun createView(
        context: Context,
        username: String,
        state: ClassicAccountLoginState,
        onLogin: (String) -> Unit,
        onCancel: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                ClassicAccountLoginContent(username, state, onLogin, onCancel::run)
            }
        }
    }
}

@Composable
private fun ClassicAccountLoginContent(
    username: String,
    state: ClassicAccountLoginState,
    onLogin: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(context.getString(R.string.account_login_refresh), style = MaterialTheme.typography.titleMedium)
        Text(username, style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                state.setError(null)
            },
            label = { Text(context.getString(R.string.account_create_password)) },
            singleLine = true,
            enabled = state.enabled,
            isError = state.error != null,
            supportingText = state.error?.let { message -> ({ Text(message) }) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (state.enabled) onLogin(password) }),
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { onLogin(password) }, enabled = state.enabled, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.account_login))
            }
            TextButton(onClick = onCancel, enabled = state.enabled) {
                Text(context.getString(R.string.dialog_negative))
            }
        }
    }
}
