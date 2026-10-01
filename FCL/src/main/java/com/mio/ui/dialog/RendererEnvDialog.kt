package com.mio.ui.dialog

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.mio.plugin.RendererPlugin.EnvSpec
import com.mio.plugin.RendererPlugin.EnvType
import com.mio.plugin.RendererPlugin.EnvValue
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

/** Configure selectable, customizable, and toggleable renderer environment values. */
class RendererEnvDialog(
    context: Context,
    title: String,
    private val specs: List<EnvSpec>,
    private val onConfirm: (Map<String, EnvValue>) -> Unit
) : FCLDialog(context) {
    init {
        val point = Point()
        window?.windowManager?.defaultDisplay?.getSize(point)
        val params = window?.attributes
        params?.width = (500 * context.resources.displayMetrics.density).toInt()
        params?.height = if (point.x.toFloat() / point.y.toFloat() >= 1.5f) {
            WindowManager.LayoutParams.MATCH_PARENT
        } else {
            point.y / 2
        }
        window?.attributes = params
        setContentView(RendererEnvCompose.createView(context, title, specs, onConfirm, this::dismiss))
    }
}

private object RendererEnvCompose {
    fun createView(
        context: Context,
        title: String,
        specs: List<EnvSpec>,
        onConfirm: (Map<String, EnvValue>) -> Unit,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                RendererEnvContent(title, specs, onConfirm, onDismiss)
            }
        }
    }
}

@Composable
private fun RendererEnvContent(
    title: String,
    specs: List<EnvSpec>,
    onConfirm: (Map<String, EnvValue>) -> Unit,
    onDismiss: Runnable
) {
    val context = LocalContext.current
    val enabledValues = remember(specs) {
        mutableStateMapOf<String, Boolean>().apply { specs.forEach { put(it.key, it.enabled) } }
    }
    val textValues = remember(specs) {
        mutableStateMapOf<String, String>().apply { specs.forEach { put(it.key, it.value) } }
    }
    var selectedValues by remember(specs) {
        mutableStateOf(specs.associate { it.key to it.value })
    }
    var expandedKey by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            specs.forEach { spec ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(spec.title, modifier = Modifier.weight(1f))
                        when (spec.type) {
                            EnvType.SELECTABLE -> if (spec.checkable) {
                                Switch(
                                    checked = enabledValues[spec.key] ?: false,
                                    onCheckedChange = { enabledValues[spec.key] = it }
                                )
                            }
                            EnvType.TOGGLEABLE -> Switch(
                                checked = enabledValues[spec.key] ?: false,
                                onCheckedChange = { enabledValues[spec.key] = it }
                            )
                            EnvType.CUSTOMIZABLE -> Unit
                        }
                    }
                    when (spec.type) {
                        EnvType.SELECTABLE -> {
                            if (spec.options.isNotEmpty()) {
                                TextButton(onClick = { expandedKey = spec.key }) {
                                    Text(selectedValues[spec.key].orEmpty())
                                }
                                DropdownMenu(
                                    expanded = expandedKey == spec.key,
                                    onDismissRequest = { expandedKey = null }
                                ) {
                                    spec.options.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                selectedValues = selectedValues + (spec.key to option)
                                                expandedKey = null
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        EnvType.CUSTOMIZABLE -> OutlinedTextField(
                            value = textValues[spec.key].orEmpty(),
                            onValueChange = { textValues[spec.key] = it },
                            placeholder = { spec.defaultValue?.let { Text(it) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        EnvType.TOGGLEABLE -> Unit
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss::run) {
                Text(context.getString(R.string.button_cancel))
            }
            Button(
                onClick = {
                    val result = specs.associate { spec ->
                        spec.key to when (spec.type) {
                            EnvType.SELECTABLE -> EnvValue(
                                enabled = if (spec.checkable) enabledValues[spec.key] else null,
                                value = selectedValues[spec.key]
                            )
                            EnvType.CUSTOMIZABLE -> EnvValue(value = textValues[spec.key].orEmpty())
                            EnvType.TOGGLEABLE -> EnvValue(enabled = enabledValues[spec.key])
                        }
                    }
                    onConfirm(result)
                    onDismiss.run()
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
