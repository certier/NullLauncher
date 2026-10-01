package com.mio.ui.dialog

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcl.control.FCLInput
import com.tungsten.fcl.control.SelectKeycodeDialog
import com.tungsten.fcl.control.gamepad.GamepadEmulatedButton
import com.tungsten.fcl.control.gamepad.GamepadMap
import com.tungsten.fclcore.fakefx.collections.FXCollections
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class GamepadMapDialog(context: Context, private val fclInput: FCLInput) : FCLDialog(context) {
    init {
        window?.setLayout((400 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.MATCH_PARENT)
        setContentView(ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    GamepadMapContent(
                        gamepadMap = fclInput.gamepad.currentMap,
                        onConfirm = {
                            fclInput.gamepad.saveMapper()
                            dismiss()
                        },
                        onCancel = ::dismiss
                    )
                }
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}

private data class GamepadMapItem(val icon: Int, val button: GamepadEmulatedButton)

@Composable
private fun GamepadMapContent(
    gamepadMap: GamepadMap,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val items = listOf(
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.button_a, gamepadMap.BUTTON_A),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.button_b, gamepadMap.BUTTON_B),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.button_x, gamepadMap.BUTTON_X),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.button_y, gamepadMap.BUTTON_Y),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.button_start, gamepadMap.BUTTON_START),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.button_select, gamepadMap.BUTTON_SELECT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.shoulder_left, gamepadMap.SHOULDER_LEFT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.shoulder_right, gamepadMap.SHOULDER_RIGHT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.trigger_left, gamepadMap.TRIGGER_LEFT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.trigger_right, gamepadMap.TRIGGER_RIGHT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.stick_left_click, gamepadMap.THUMBSTICK_LEFT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.stick_right_click, gamepadMap.THUMBSTICK_RIGHT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.dpad_up, gamepadMap.DPAD_UP),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.dpad_down, gamepadMap.DPAD_DOWN),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.dpad_left, gamepadMap.DPAD_LEFT),
        GamepadMapItem(fr.spse.gamepad_remapper.R.drawable.dpad_right, gamepadMap.DPAD_RIGHT)
    )
    Column(Modifier.fillMaxWidth().padding(10.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items) { item ->
                Card(shape = RoundedCornerShape(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = {
                            val keycodes = FXCollections.observableList(item.button.keycodes)
                            SelectKeycodeDialog(context, keycodes, false, true).show()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_settings_24),
                                contentDescription = context.getString(R.string.menu_settings_gamepad_button_binding)
                            )
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onCancel) { Text(context.getString(R.string.button_cancel)) }
            Button(onClick = onConfirm, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
