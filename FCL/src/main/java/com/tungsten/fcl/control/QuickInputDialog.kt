package com.tungsten.fcl.control

import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tungsten.fcl.control.data.QuickInputTexts
import com.tungsten.fclauncher.bridge.FCLBridge
import com.tungsten.fclauncher.keycodes.FCLKeycodes
import com.tungsten.fclauncher.keycodes.MinecraftKeyBindingMapper
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuickInputDialog(private val activity: AppCompatActivity, private val menu: GameMenu) :
class QuickInputDialog(private val activity: AppCompatActivity, private val menu: GameMenu) : FCLDialog(activity) {
    private var inputTexts by mutableStateOf(QuickInputTexts.getInputTexts().toList())

    init {
        setCancelable(false)
        val composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    QuickInputContent(
                        inputTexts = inputTexts,
                        onSelect = ::sendInput,
                        onDelete = ::deleteInput,
                        onAdd = { AddInputTextDialog(activity, ::refreshList).show() },
                        onClose = ::dismiss
                    )
                }
            }
        }
        setContentView(composeView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        window?.setLayout(
            (400 * activity.resources.displayMetrics.density).toInt(),
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun refreshList() {
        inputTexts = QuickInputTexts.getInputTexts().toList()
    }

    private fun deleteInput(input: String) {
        QuickInputTexts.removeInputText(input)
        refreshList()
    }

    private fun sendInput(input: String) {
        if (input.isNotEmpty()) {
            if (menu.cursorMode == FCLBridge.CursorEnabled) {
                input.forEach { character -> menu.input.sendChar(character) }
            } else {
                val gameOption = menu.gameOption
                menu.input.sendBoundKeyEvent(gameOption, MinecraftKeyBindingMapper.BINDING_CHAT, FCLKeycodes.KEY_T, true)
                menu.input.sendBoundKeyEvent(gameOption, MinecraftKeyBindingMapper.BINDING_CHAT, FCLKeycodes.KEY_T, false)
                activity.lifecycleScope.launch {
                    delay(50)
                    input.forEach { character -> menu.input.sendChar(character) }
                    menu.input.sendKeyEvent(FCLKeycodes.KEY_ENTER, true)
                    menu.input.sendKeyEvent(FCLKeycodes.KEY_ENTER, false)
                }
            }
        }
        dismiss()
    }
}

@Composable
private fun QuickInputContent(
    inputTexts: List<String>,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit,
    onAdd: () -> Unit,
    onClose: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Card(shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Text(
                text = context.getString(R.string.quick_input_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
            )
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(inputTexts) { storedText ->
                    val parts = storedText.split("&*&", limit = 2)
                    val label = parts[0]
                    val value = parts.getOrElse(1) { parts[0] }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = label,
                            modifier = Modifier.weight(1f).clickable { onSelect(value) }.padding(10.dp),
                            maxLines = 1
                        )
                        IconButton(onClick = { onDelete(storedText) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_delete_24),
                                contentDescription = context.getString(R.string.button_remove)
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onAdd) { Text(context.getString(R.string.quick_input_add)) }
                Button(onClick = onClose, shape = RoundedCornerShape(4.dp)) {
                    Text(context.getString(R.string.dialog_positive))
                }
            }
        }
    }
}
