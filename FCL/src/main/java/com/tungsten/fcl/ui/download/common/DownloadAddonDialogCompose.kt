package com.tungsten.fcl.ui.download.common

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class DownloadAddonDialogState(initialName: String) {
    var name by mutableStateOf(initialName)
}

object DownloadAddonDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        state: DownloadAddonDialogState,
        onSubmit: Runnable,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                DownloadAddonContent(state, onSubmit::run, onDismiss::run)
            }
        }
    }
}

@Composable
private fun DownloadAddonContent(
    state: DownloadAddonDialogState,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(context.getString(R.string.archive_name), style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.dialog_negative))
            }
            Button(onClick = onSubmit, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
