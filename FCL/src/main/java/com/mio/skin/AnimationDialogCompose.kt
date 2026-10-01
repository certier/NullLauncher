package com.mio.skin

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

object AnimationDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        currentId: String?,
        solidLayerEnabled: Boolean,
        upperBodySeparated: Boolean,
        animations: List<SkinAnimations.Entry>,
        onSelected: OnAnimationSelectedListener,
        onSolidLayerToggled: OnSolidLayerToggledListener,
        onBodySeparationToggled: OnBodySeparationToggledListener,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                AnimationDialogContent(
                    currentId,
                    solidLayerEnabled,
                    upperBodySeparated,
                    animations,
                    onSelected,
                    onSolidLayerToggled,
                    onBodySeparationToggled,
                    onDismiss
                )
            }
        }
    }
}

@Composable
private fun AnimationDialogContent(
    currentId: String?,
    initialSolidLayerEnabled: Boolean,
    initialUpperBodySeparated: Boolean,
    animations: List<SkinAnimations.Entry>,
    onSelected: OnAnimationSelectedListener,
    onSolidLayerToggled: OnSolidLayerToggledListener,
    onBodySeparationToggled: OnBodySeparationToggledListener,
    onDismiss: Runnable
) {
    var solidLayerEnabled by remember { mutableStateOf(initialSolidLayerEnabled) }
    var upperBodySeparated by remember { mutableStateOf(initialUpperBodySeparated) }

    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(
            text = stringResource(com.tungsten.fcl.R.string.skin_settings),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
        )
        SettingRow(
            title = stringResource(com.tungsten.fcl.R.string.skin_solid_layer),
            checked = solidLayerEnabled,
            onCheckedChange = {
                solidLayerEnabled = it
                onSolidLayerToggled.onToggled(it)
            }
        )
        SettingRow(
            title = stringResource(com.tungsten.fcl.R.string.skin_body_separation),
            checked = upperBodySeparated,
            onCheckedChange = {
                upperBodySeparated = it
                onBodySeparationToggled.onToggled(it)
            }
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(animations, key = { it.id }) { animation ->
                val selected = animation.id == currentId
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onSelected.onSelected(animation.id)
                        onDismiss.run()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = stringResource(animation.nameRes),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
