package com.tungsten.fcl.control

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mio.datastore.GameItemBarSetting
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class GameItemBarSettingDialog(
    context: Context,
    val setting: GameItemBarSetting,
    val callback: (GameItemBarSetting) -> Unit
) : FCLDialog(context) {
    init {
        window?.setLayout((400 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawableResource(R.drawable.bg_game_menu)
        setContentView(ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    GameItemBarSettingsContent(
                        initialSetting = setting,
                        onChange = callback,
                        onClose = ::dismiss
                    )
                }
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}

@Composable
private fun GameItemBarSettingsContent(
    initialSetting: GameItemBarSetting,
    onChange: (GameItemBarSetting) -> Unit,
    onClose: () -> Unit
) {
    var currentSetting by remember(initialSetting) { mutableStateOf(initialSetting) }
    val context = LocalContext.current
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game_menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GameItemBarSettingRow(context.getString(R.string.slide_selection), currentSetting.slideSelection) {
                currentSetting = currentSetting.copy(slideSelection = it)
                onChange(currentSetting)
            }
            GameItemBarSettingRow(context.getString(R.string.swap_hands), currentSetting.doubleTapSwapHands) {
                currentSetting = currentSetting.copy(doubleTapSwapHands = it)
                onChange(currentSetting)
            }
            Button(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
                Text(context.getString(R.string.close))
            }
        }
    }
}

@Composable
private fun GameItemBarSettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}