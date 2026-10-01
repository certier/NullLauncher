package com.tungsten.fcl.ui.controller

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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

class ControllerInfoDialogState(
    name: String,
    version: String,
    versionCode: String,
    author: String,
    description: String,
    val title: String
) {
    var name by mutableStateOf(name)
    var version by mutableStateOf(version)
    var versionCode by mutableStateOf(versionCode)
    var author by mutableStateOf(author)
    var description by mutableStateOf(description)
    var moreInfo by mutableStateOf(false)
}

object ControllerInfoDialogCompose {
    @JvmStatic
    fun createView(context: Context, state: ControllerInfoDialogState, onSubmit: Runnable, onDismiss: Runnable): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    ControllerInfoContent(state, onSubmit::run, onDismiss::run)
                }
            }
        }
}

@Composable
private fun ControllerInfoContent(state: ControllerInfoDialogState, onSubmit: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(state.title, style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },
            label = { Text(context.getString(R.string.control_info_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = state.moreInfo, onCheckedChange = { state.moreInfo = it })
            Text(context.getString(R.string.control_info_more))
        }
        if (state.moreInfo) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = state.version,
                    onValueChange = { state.version = it },
                    label = { Text(context.getString(R.string.control_info_version)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.versionCode,
                    onValueChange = { value -> if (value.all(Char::isDigit)) state.versionCode = value },
                    label = { Text(context.getString(R.string.control_info_version_code)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.author,
                    onValueChange = { state.author = it },
                    label = { Text(context.getString(R.string.control_info_author)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { state.description = it },
                    label = { Text(context.getString(R.string.control_info_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) { Text(context.getString(R.string.dialog_negative)) }
            Button(onClick = onSubmit, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
