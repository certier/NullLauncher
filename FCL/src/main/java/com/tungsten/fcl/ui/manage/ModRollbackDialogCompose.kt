package com.tungsten.fcl.ui.manage

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
import com.tungsten.fclcore.mod.LocalModFile
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.util.function.Consumer

object ModRollbackDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        versions: List<LocalModFile>,
        onSelect: Consumer<LocalModFile>,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                ModRollbackContent(versions, onSelect, onDismiss)
            }
        }
    }
}

@Composable
private fun ModRollbackContent(
    versions: List<LocalModFile>,
    onSelect: Consumer<LocalModFile>,
    onDismiss: Runnable
) {
    val context = LocalContext.current
    Card(shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(context.getString(R.string.archive_version), style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false).fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(versions) { version ->
                    Text(
                        text = version.version,
                        modifier = Modifier.fillMaxWidth().clickable { onSelect.accept(version) }
                            .padding(10.dp),
                        maxLines = 1
                    )
                }
            }
            TextButton(onClick = onDismiss::run, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
                Text(context.getString(R.string.dialog_negative))
            }
        }
    }
}
