package com.tungsten.fcllibrary.component.dialog

import android.content.Context
import android.text.util.Linkify
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.HorizontalDivider
import androidx.core.text.util.LinkifyCompat
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.ThemeEngine
import java.util.function.IntConsumer

data class FCLAlertActionState(val text: String?, val visible: Boolean)

class FCLAlertDialogState(initialTitle: String, initialLevel: Int) {
    var title by mutableStateOf(initialTitle)
    var message by mutableStateOf<CharSequence?>("")
    var alertLevelCode by mutableStateOf(initialLevel)
    var autoLinkEnabled by mutableStateOf(false)
    var actions by mutableStateOf(List(4) { FCLAlertActionState(null, false) })

    fun setAction(index: Int, text: String?) {
        actions = actions.toMutableList().also { it[index] = FCLAlertActionState(text, true) }
    }
}

object FCLAlertDialogCompose {
    @JvmStatic
    fun createView(context: Context, state: FCLAlertDialogState, onAction: IntConsumer): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent { AlertDialogContent(state, onAction) }
        }
}

@Composable
private fun AlertDialogContent(state: FCLAlertDialogState, onAction: IntConsumer) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val themeData by ThemeEngine.theme.collectAsState()
    val primary = Color(themeData?.getColor() ?: 0xFF777777.toInt())
    val secondary = Color(themeData?.getColor2() ?: 0xFF4F6367.toInt())
    val onPrimary = Color(themeData?.autoTint ?: android.graphics.Color.WHITE)
    val colorScheme = if (ThemeEngine.isNightMode(context)) {
        darkColorScheme(primary = primary, onPrimary = onPrimary, secondary = secondary)
    } else {
        lightColorScheme(primary = primary, onPrimary = onPrimary, secondary = secondary)
    }
    val iconResource = if (state.alertLevelCode == 0) {
        R.drawable.ic_baseline_warning_24
    } else {
        R.drawable.ic_baseline_info_24
    }

    MaterialTheme(colorScheme = colorScheme) {
        Card(
            modifier = Modifier
                .widthIn(min = 350.dp)
                .heightIn(min = 150.dp, max = (configuration.screenHeightDp - 30).coerceAtLeast(150).dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(Modifier.fillMaxWidth().padding(15.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(iconResource),
                        contentDescription = null,
                        tint = if (state.alertLevelCode == 0) colorScheme.error else colorScheme.primary
                    )
                    Text(
                        text = state.title,
                        modifier = Modifier.padding(start = 10.dp),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                HorizontalDivider(Modifier.padding(top = 12.dp))
                AndroidView(
                    factory = { viewContext ->
                        TextView(viewContext).apply {
                            textIsSelectable = true
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        }
                    },
                    update = { textView ->
                        textView.text = state.message
                        textView.setTextColor(themeData?.autoTint ?: android.graphics.Color.BLACK)
                        if (state.autoLinkEnabled) {
                            LinkifyCompat.addLinks(textView, Linkify.WEB_URLS)
                            textView.movementMethod = LinkMovementMethod.getInstance()
                            textView.setLinkTextColor(themeData?.getColor() ?: colorScheme.primary.toArgb())
                        } else {
                            textView.movementMethod = null
                        }
                    },
                    modifier = Modifier.weight(1f, fill = false)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 12.dp)
                )
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    state.actions.forEachIndexed { index, action ->
                        if (action.visible) {
                            Button(
                                onClick = { onAction.accept(index) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = action.text.orEmpty(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}