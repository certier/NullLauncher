package com.mio.ui.dialog

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

object MioLibPatcherCompose {
    @JvmStatic
    fun createView(
        context: Context,
        alc10: Boolean,
        sableRapier: Boolean,
        asmBackport: Boolean,
        onConfirm: (Boolean, Boolean, Boolean) -> Unit,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                MioLibPatcherContent(alc10, sableRapier, asmBackport, onConfirm, onDismiss::run)
            }
        }
    }
}

@Composable
private fun MioLibPatcherContent(
    initialAlc10: Boolean,
    initialSableRapier: Boolean,
    initialAsmBackport: Boolean,
    onConfirm: (Boolean, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var alc10 by remember { mutableStateOf(initialAlc10) }
    var sableRapier by remember { mutableStateOf(initialSableRapier) }
    var asmBackport by remember { mutableStateOf(initialAsmBackport) }
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.plugin_miolibpatcher_name), style = MaterialTheme.typography.titleLarge)
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().heightIn(max = 480.dp)
                .verticalScroll(rememberScrollState()).padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeatureRow(
                title = context.getString(R.string.plugin_miolibpatcher_alc10),
                description = context.getString(R.string.plugin_miolibpatcher_alc10_desc),
                checked = alc10,
                onCheckedChange = { alc10 = it }
            )
            FeatureRow(
                title = context.getString(R.string.plugin_miolibpatcher_sable),
                description = context.getString(R.string.plugin_miolibpatcher_sable_desc),
                checked = sableRapier,
                onCheckedChange = { sableRapier = it }
            )
            FeatureRow(
                title = context.getString(R.string.plugin_miolibpatcher_asm),
                description = context.getString(R.string.plugin_miolibpatcher_asm_desc),
                checked = asmBackport,
                onCheckedChange = { asmBackport = it }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text(context.getString(R.string.button_cancel)) }
            Button(
                onClick = { onConfirm(alc10, sableRapier, asmBackport) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}

@Composable
private fun FeatureRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
        Text(description, style = MaterialTheme.typography.bodySmall)
    }
}
