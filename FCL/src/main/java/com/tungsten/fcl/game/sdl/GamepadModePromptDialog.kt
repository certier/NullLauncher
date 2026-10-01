/*
 * Fold Craft Launcher
 * 首次手柄输入时的输入模式选择弹窗（对齐 ZalithLauncher2 的 GamepadModePromptDialog）
 */
package com.tungsten.fcl.game.sdl

import android.app.Activity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.lang.ref.WeakReference

/**
 * 首次手柄输入时弹窗选择输入模式（确认前所有手柄输入都会被吞掉）。
 */
class GamepadModePromptDialog private constructor(activity: Activity) : FCLDialog(activity) {
    private var mode by mutableStateOf(SdlSettings.gamepadInputMode.value)

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setLayout((380 * activity.resources.displayMetrics.density).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    GamepadModePromptContent(
                        mode = mode,
                        onModeChange = { mode = it },
                        onConfirm = {
                            SdlSettings.setGamepadInputMode(mode)
                            SdlSettings.setGamepadInputModePrompted(true)
                            dismiss()
                        }
                    )
                }
            }
        }, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    companion object {
        private var sInstance: WeakReference<GamepadModePromptDialog>? = null

        @JvmStatic
        fun checkAndShow(activity: Activity): Boolean {
            if (SdlSettings.isGamepadInputModePrompted()) return false
            val existing = sInstance?.get()
            if (existing != null && existing.isShowing) return true
            if (activity.isFinishing || activity.isDestroyed) return false
            val dialog = GamepadModePromptDialog(activity)
            sInstance = WeakReference(dialog)
            dialog.show()
            return true
        }
    }
}

@androidx.compose.runtime.Composable
private fun GamepadModePromptContent(
    mode: GamepadInputMode,
    onModeChange: (GamepadInputMode) -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    Card(shape = RoundedCornerShape(4.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = context.getString(R.string.gamepad_mode_prompt_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(context.getString(R.string.gamepad_mode_prompt_description))
            ModeOption(
                title = context.getString(R.string.menu_settings_gamepad_input_mode_mapped),
                summary = context.getString(R.string.gamepad_mode_prompt_mapped_summary),
                selected = mode == GamepadInputMode.MAPPED,
                onClick = { onModeChange(GamepadInputMode.MAPPED) }
            )
            ModeOption(
                title = context.getString(R.string.menu_settings_gamepad_input_mode_sdl_direct),
                summary = context.getString(R.string.gamepad_mode_prompt_sdl_direct_summary),
                selected = mode == GamepadInputMode.SDL_DIRECT,
                onClick = { onModeChange(GamepadInputMode.SDL_DIRECT) }
            )
            Text(context.getString(R.string.gamepad_mode_prompt_hint), style = MaterialTheme.typography.bodySmall)
            Button(
                onClick = onConfirm,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ModeOption(title: String, summary: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Column(Modifier.padding(start = 8.dp)) {
            Text(title)
            Text(summary, style = MaterialTheme.typography.bodySmall)
        }
    }
}