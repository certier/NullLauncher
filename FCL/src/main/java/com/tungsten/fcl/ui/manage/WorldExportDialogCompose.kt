package com.tungsten.fcl.ui.manage

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
import com.tungsten.fclcore.util.StringUtils
import com.tungsten.fclcore.util.platform.OperatingSystem
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.io.File

class WorldExportDialogState(fileName: String, name: String) {
    var fileName by mutableStateOf(fileName)
    var name by mutableStateOf(name)
}

object WorldExportDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        parentPath: String,
        state: WorldExportDialogState,
        onExport: Runnable,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                WorldExportContent(parentPath, state, onExport::run, onDismiss::run)
            }
        }
    }
}

@Composable
private fun WorldExportContent(
    parentPath: String,
    state: WorldExportDialogState,
    onExport: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val canExport = state.name.isNotEmpty() && StringUtils.isNotBlank(state.fileName) &&
        OperatingSystem.isNameValid(state.fileName) && !File(parentPath, state.fileName).exists()
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(context.getString(R.string.world_export), style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = state.fileName,
            onValueChange = { state.fileName = it },
            label = { Text(context.getString(R.string.archive_name)) },
            singleLine = true,
            isError = state.fileName.isNotEmpty() && !OperatingSystem.isNameValid(state.fileName),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },
            label = { Text(context.getString(R.string.world_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.dialog_negative))
            }
            Button(onClick = onExport, enabled = canExport, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
