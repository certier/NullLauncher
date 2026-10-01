package com.tungsten.fcl.ui.download.modpack

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

object ModpackUrlDialogCompose {
    @JvmStatic
    fun createView(context: Context, callback: ModpackUrlDialog.Callback, onDismiss: Runnable): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    ModpackUrlContent(callback::onPositive, onDismiss::run)
                }
            }
        }
}

@Composable
private fun ModpackUrlContent(onSubmit: (String) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var url by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = context.getString(R.string.modpack_choose_remote_tooltip),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.dialog_negative))
            }
            Button(
                onClick = { if (url.isNotBlank()) onSubmit(url) },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}