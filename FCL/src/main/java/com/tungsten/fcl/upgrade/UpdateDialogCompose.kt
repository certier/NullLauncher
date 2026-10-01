package com.tungsten.fcl.upgrade

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.util.function.Consumer

object UpdateDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        version: RemoteVersion,
        onIgnore: Runnable,
        onUpdate: Runnable,
        onNetdisk: Runnable,
        onDismiss: Runnable,
        onLongPress: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                UpdateContent(version, onIgnore, onUpdate, onNetdisk, onDismiss, onLongPress)
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun UpdateContent(
    version: RemoteVersion,
    onIgnore: Runnable,
    onUpdate: Runnable,
    onNetdisk: Runnable,
    onDismiss: Runnable,
    onLongPress: Runnable
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    Column(
        modifier = Modifier.widthIn(min = 450.dp)
            .heightIn(max = (config.screenHeightDp * 0.9f).dp)
            .padding(12.dp)
    ) {
        Text(
            text = context.getString(R.string.update_exist),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.weight(1f, fill = false).fillMaxWidth()
                .padding(vertical = 10.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(context.getString(R.string.update_version, version.getVersionName()))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(context.getString(R.string.update_type, version.getDisplayType(context)), modifier = Modifier.weight(1f))
                Text(context.getString(R.string.update_date, version.getDate()), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
            Text(context.getString(R.string.update_description, version.getDisplayDescription(context)))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onIgnore::run) { Text(context.getString(R.string.update_ignore)) }
            Box(Modifier.weight(1f))
            TextButton(onClick = onNetdisk::run) { Text(context.getString(R.string.update_netdisk)) }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .combinedClickable(onClick = onUpdate::run, onLongClick = onLongPress::run)
                    .semantics { role = Role.Button }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(context.getString(R.string.update), color = MaterialTheme.colorScheme.onPrimary)
            }
            TextButton(onClick = onDismiss::run) { Text(context.getString(R.string.dialog_negative)) }
        }
    }
}
