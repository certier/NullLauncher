package com.mio.ui.dialog

import android.content.Context
import android.graphics.Point
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mio.data.Renderer
import com.mio.manager.RendererManager
import com.tungsten.fcl.R
import com.tungsten.fcl.setting.Profiles
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.util.function.Consumer

class RendererSelectDialog(
    context: Context,
    val isGlobal: Boolean,
    private val callback: Consumer<String>
) : FCLDialog(context) {
    private val renderers = mutableStateListOf<Renderer>()
    private val currentId = if (isGlobal) Profiles.getSelectedProfile().globalVersionSetting.renderer
    else Profiles.getSelectedProfile().versionSetting.renderer

    init {
        renderers.addAll(RendererManager.rendererList)
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
        setContentView(RendererSelectCompose.createView(context, renderers, currentId, ::refreshRenderers, ::selectRenderer, this::dismiss),
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun refreshRenderers() {
        RendererManager.refresh(context)
        renderers.clear()
        renderers.addAll(RendererManager.rendererList)
    }

    private fun selectRenderer(renderer: Renderer) {
        val setting = if (isGlobal) Profiles.getSelectedProfile().globalVersionSetting
        else Profiles.getSelectedProfile().versionSetting
        setting.renderer = renderer.id
        dismiss()
        callback.accept(renderer.des)
    }
}

private object RendererSelectCompose {
    fun createView(
        context: Context,
        renderers: List<Renderer>,
        currentId: String,
        onRefresh: () -> Unit,
        onSelect: (Renderer) -> Unit,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                RendererSelectContent(renderers, currentId, onRefresh, onSelect, onDismiss)
            }
        }
    }
}

@Composable
private fun RendererSelectContent(
    renderers: List<Renderer>,
    currentId: String,
    onRefresh: () -> Unit,
    onSelect: (Renderer) -> Unit,
    onDismiss: Runnable
) {
    val context = LocalContext.current
    val selectedIndex = renderers.indexOfFirst { it.isEqual(currentId) }.coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.settings_fcl_renderer), style = MaterialTheme.typography.titleLarge)
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(renderers, key = { it.id }) { renderer ->
                val selected = renderer.isEqual(currentId)
                val maxVersion = renderer.displayMaxMCver.ifEmpty { renderer.maxMCver }
                val version = when {
                    renderer.minMCver.isNotEmpty() && maxVersion.isNotEmpty() -> "${renderer.minMCver}~$maxVersion"
                    renderer.minMCver.isNotEmpty() -> ">=${renderer.minMCver}"
                    maxVersion.isNotEmpty() -> "<=$maxVersion"
                    else -> context.getString(R.string.message_unknown)
                }
                val source = renderer.source.ifEmpty { context.getString(R.string.renderer_source_builtin) }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(renderer) },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(renderer.des, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "${context.getString(R.string.supported_mc_version)} $version",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${context.getString(R.string.renderer_source)} $source",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (selected) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_done_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onRefresh, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.action_refresh))
            }
            TextButton(onClick = onDismiss::run) {
                Text(context.getString(R.string.button_cancel))
            }
        }
    }
}
