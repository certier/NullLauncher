package com.tungsten.fcl.upgrade

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.ThemeEngine

class ChangelogDialog(context: Context, private val version: RemoteVersion) : FCLDialog(context) {
    init {
        setCancelable(false)
        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent { ChangelogContent(version, ::dismiss) }
        }
        setContentView(composeView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        window?.setLayout(
            (450 * context.resources.displayMetrics.density).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}

@Composable
private fun ChangelogContent(version: RemoteVersion, onDismiss: () -> Unit) {
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

    MaterialTheme(colorScheme = colorScheme) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 450.dp)
                    .heightIn(max = (configuration.screenHeightDp * 0.7f).dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(R.string.update_changelog),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
                Column(
                    modifier = Modifier.weight(1f, fill = false)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(context.getString(R.string.update_version, version.getVersionName()))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = context.getString(R.string.update_type, version.getDisplayType(context)),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = context.getString(R.string.update_date, version.getDate()),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                    Text(context.getString(R.string.update_description, version.getDisplayDescription(context)))
                }
                HorizontalDivider(Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(context.getString(R.string.dialog_positive))
                    }
                }
            }
        }
    }
}