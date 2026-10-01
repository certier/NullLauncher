package com.tungsten.fcl.control

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

object EditViewDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        title: String,
        infoView: View,
        eventView: View,
        cloneable: Boolean,
        onClone: Runnable,
        onDelete: Runnable,
        onSave: Runnable,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                EditViewContent(title, infoView, eventView, cloneable, onClone, onDelete, onSave, onDismiss)
            }
        }
    }
}

@Composable
private fun EditViewContent(
    title: String,
    infoView: View,
    eventView: View,
    cloneable: Boolean,
    onClone: Runnable,
    onDelete: Runnable,
    onSave: Runnable,
    onDismiss: Runnable
) {
    val context = LocalContext.current
    var selectedPane by remember { mutableIntStateOf(0) }
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game_menu_inset),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(Modifier.fillMaxSize().padding(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
            Row(Modifier.weight(1f).fillMaxWidth().padding(vertical = 10.dp)) {
                Column(Modifier.width(48.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                    IconButton(onClick = { selectedPane = 0 }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_settings_24),
                            contentDescription = title,
                            tint = if (selectedPane == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { selectedPane = 1 }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_keyboard_24),
                            contentDescription = context.getString(R.string.edit_button_title),
                            tint = if (selectedPane == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                AndroidView(
                    factory = { viewContext ->
                        FrameLayout(viewContext).apply {
                            addView(infoView, FrameLayout.LayoutParams(-1, -2))
                            addView(eventView, FrameLayout.LayoutParams(-1, -2))
                        }
                    },
                    update = {
                        infoView.visibility = if (selectedPane == 0) View.VISIBLE else View.GONE
                        eventView.visibility = if (selectedPane == 1) View.VISIBLE else View.GONE
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (cloneable) {
                    TextButton(onClick = onClone::run, modifier = Modifier.weight(1f)) {
                        Text(context.getString(R.string.edit_view_clone), maxLines = 1)
                    }
                    TextButton(onClick = onDelete::run, modifier = Modifier.weight(1f)) {
                        Text(context.getString(R.string.edit_view_delete), maxLines = 1)
                    }
                }
                TextButton(onClick = onDismiss::run, modifier = Modifier.weight(1f)) {
                    Text(context.getString(R.string.dialog_negative), maxLines = 1)
                }
                Button(onClick = onSave::run, modifier = Modifier.weight(1f), shape = RoundedCornerShape(4.dp)) {
                    Text(context.getString(R.string.dialog_positive), maxLines = 1)
                }
            }
        }
    }
}
