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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class EditViewGroupDialogState(name: String, visible: Boolean) {
    var name by mutableStateOf(name)
    var visible by mutableStateOf(visible)
}

object EditViewGroupDialogCompose {
    @JvmStatic
    fun createView(context: Context, state: EditViewGroupDialogState, onSubmit: Runnable, onDismiss: Runnable): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    EditViewGroupContent(state, onSubmit::run, onDismiss::run)
                }
            }
        }
}

@Composable
private fun EditViewGroupContent(state: EditViewGroupDialogState, onSubmit: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(context.getString(R.string.menu_control_view_group_add), style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },
            label = { Text(context.getString(R.string.menu_control_view_group_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(context.getString(R.string.menu_control_view_group_visibility), style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            val labels = listOf(
                context.getString(R.string.menu_control_view_group_visible),
                context.getString(R.string.menu_control_view_group_invisible)
            )
            labels.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = state.visible == (index == 0),
                    onClick = { state.visible = index == 0 },
                    shape = SegmentedButtonDefaults.itemShape(index, labels.size)
                ) {
                    Text(label)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) { Text(context.getString(R.string.dialog_negative)) }
            Button(onClick = onSubmit, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
