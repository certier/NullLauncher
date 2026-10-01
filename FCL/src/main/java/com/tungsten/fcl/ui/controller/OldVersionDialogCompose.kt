package com.tungsten.fcl.ui.controller

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcl.control.download.ControllerVersion
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.util.function.Consumer

object OldVersionDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        versions: List<ControllerVersion.VersionInfo>,
        onSelect: Consumer<ControllerVersion.VersionInfo>,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                OldVersionContent(versions, onSelect, onDismiss)
            }
        }
    }
}

@Composable
private fun OldVersionContent(
    versions: List<ControllerVersion.VersionInfo>,
    onSelect: Consumer<ControllerVersion.VersionInfo>,
    onDismiss: Runnable
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.control_download_history), style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.weight(1f, fill = false).fillMaxWidth().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(versions) { version ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect.accept(version) },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = version.versionName,
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        TextButton(onClick = onDismiss::run, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
            Text(context.getString(R.string.dialog_negative))
        }
    }
}
