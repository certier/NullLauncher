package com.tungsten.fcl.ui.manage

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class ModInfoDialogState(
    val name: String,
    val version: String,
    val fileName: String,
    val description: String,
    val url: String,
    val hasWebsite: Boolean
) {
    var logo by mutableStateOf<Bitmap?>(null)
        private set

    fun setLogo(value: Bitmap?) {
        logo = value
    }
}

object ModInfoDialogCompose {
    @JvmStatic
    fun createView(context: Context, state: ModInfoDialogState, onWebsite: Runnable, onDismiss: Runnable): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    ModInfoContent(state, onWebsite::run, onDismiss::run)
                }
            }
        }
}

@Composable
private fun ModInfoContent(state: ModInfoDialogState, onWebsite: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val logo = state.logo
            val logoPainter = logo?.let { BitmapPainter(it.asImageBitmap()) }
                ?: painterResource(R.drawable.img_command)
            Image(
                painter = logoPainter,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                contentScale = ContentScale.Fit
            )
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(state.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                    Text(state.version, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
                Text(state.fileName, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
        }
        Text(
            text = state.description,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (state.hasWebsite) {
                TextButton(onClick = onWebsite) { Text(context.getString(R.string.mods_url)) }
            }
            Button(onClick = onDismiss, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
