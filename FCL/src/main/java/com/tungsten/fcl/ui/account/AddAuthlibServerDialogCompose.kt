package com.tungsten.fcl.ui.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.util.function.Consumer

class AddAuthlibServerDialogState {
    var url by mutableStateOf("")
    var name by mutableStateOf("")
        private set
    var address by mutableStateOf("")
        private set
    var loading by mutableStateOf(false)
        private set
    var showingServer by mutableStateOf(false)
        private set

    fun setLoading(value: Boolean) {
        loading = value
    }

    fun setResolvedServer(address: String, name: String) {
        this.address = address
        this.name = name
        showingServer = true
    }

    fun showUrlEntry() {
        showingServer = false
    }
}

object AddAuthlibServerDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        state: AddAuthlibServerDialogState,
        onNext: Runnable,
        onBack: Runnable,
        onSave: Runnable,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                AddAuthlibServerContent(state, onNext, onBack, onSave, onDismiss)
            }
        }
    }
}

@Composable
private fun AddAuthlibServerContent(
    state: AddAuthlibServerDialogState,
    onNext: Runnable,
    onBack: Runnable,
    onSave: Runnable,
    onDismiss: Runnable
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(context.getString(R.string.account_add_server), style = MaterialTheme.typography.titleMedium)
        if (state.showingServer) {
            Text(context.getString(R.string.account_add_server_url))
            Text(state.address, maxLines = 1)
            Text(context.getString(R.string.account_add_server_name))
            Text(state.name, maxLines = 1)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onBack::run) { Text(context.getString(R.string.button_prev)) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss::run) { Text(context.getString(R.string.dialog_negative)) }
                    Button(onClick = onSave::run, shape = RoundedCornerShape(4.dp)) {
                        Text(context.getString(R.string.dialog_positive))
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = state.url,
                onValueChange = { state.url = it },
                label = { Text(context.getString(R.string.account_add_server_url)) },
                singleLine = true,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss::run, enabled = !state.loading) {
                    Text(context.getString(R.string.dialog_negative))
                }
                if (state.loading) CircularProgressIndicator()
                Button(
                    onClick = onNext::run,
                    enabled = !state.loading && state.url.isNotBlank(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(context.getString(R.string.button_next))
                }
            }
        }
    }
}
