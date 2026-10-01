package com.tungsten.fcl.control

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcl.terracotta.Terracotta
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import net.burningtnt.terracotta.TerracottaAndroidAPI
import java.util.function.Consumer

class InviteCodeInputState {
    var code by mutableStateOf("")
        private set

    fun setCode(value: String) {
        code = value
    }

    fun reset() {
        code = ""
    }
}

object InviteCodeInputCompose {
    @JvmStatic
    fun createView(
        context: Context,
        state: InviteCodeInputState,
        onSubmit: Consumer<String>,
        onDismiss: Runnable,
        onInvalid: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                InviteCodeContent(state, onSubmit, onDismiss, onInvalid)
            }
        }
    }
}

@Composable
private fun InviteCodeContent(
    state: InviteCodeInputState,
    onSubmit: Consumer<String>,
    onDismiss: Runnable,
    onInvalid: Runnable
) {
    val context = LocalContext.current
    val roomType = Terracotta.parseRoomCode(state.code)
    val hint = when {
        state.code.isEmpty() -> null
        roomType == TerracottaAndroidAPI.RoomType.TERRACOTTA_LEGACY -> context.getString(R.string.terracotta_status_waiting_guest_prompt_terracotta_legacy)
        roomType == TerracottaAndroidAPI.RoomType.PCL2CE -> context.getString(R.string.terracotta_status_waiting_guest_prompt_pcl2ce)
        roomType == TerracottaAndroidAPI.RoomType.SCAFFOLDING -> context.getString(R.string.terracotta_status_waiting_guest_prompt_scaffolding)
        else -> context.getString(R.string.terracotta_status_waiting_guest_prompt_invalid)
    }
    val hintColor = when (roomType) {
        TerracottaAndroidAPI.RoomType.TERRACOTTA_LEGACY,
        TerracottaAndroidAPI.RoomType.PCL2CE -> Color.Yellow
        TerracottaAndroidAPI.RoomType.SCAFFOLDING -> Color.Green
        else -> Color.Red
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = context.getString(R.string.terracotta_status_waiting_guest_prompt_title),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = state.code,
            onValueChange = state::setCode,
            placeholder = { Text(context.getString(R.string.terracotta_code_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        hint?.let {
            Text(it, modifier = Modifier.fillMaxWidth(), color = hintColor)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss::run) {
                Text(context.getString(R.string.dialog_negative))
            }
            Button(
                onClick = {
                    if (roomType != null) onSubmit.accept(state.code) else onInvalid.run()
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
