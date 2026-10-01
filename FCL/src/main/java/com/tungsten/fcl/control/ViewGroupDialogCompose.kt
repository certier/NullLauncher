package com.tungsten.fcl.control

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcl.control.data.ControlViewGroup
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

interface ViewGroupDialogActions {
    fun onSelectionChange(group: ControlViewGroup, checked: Boolean)
    fun onMove(from: Int, to: Int)
    fun onEdit(group: ControlViewGroup)
    fun onDelete(group: ControlViewGroup)
    fun onAdd()
    fun onConfirm()
    fun onDismiss()
}

class ViewGroupDialogState(groups: List<ControlViewGroup>, selectedIds: Set<String>) {
    val groups = mutableStateListOf<ControlViewGroup>().apply { addAll(groups) }
    var selectedIds by mutableStateOf(selectedIds)
        private set

    fun setSelected(group: ControlViewGroup, selected: Boolean) {
        selectedIds = if (selected) selectedIds + group.id else selectedIds - group.id
    }

    fun move(from: Int, to: Int) {
        if (from !in groups.indices || to !in groups.indices) return
        val moved = groups.removeAt(from)
        groups.add(to, moved)
    }

    fun refresh(updated: List<ControlViewGroup>) {
        groups.clear()
        groups.addAll(updated)
    }
}

object ViewGroupDialogCompose {
    @JvmStatic
    fun createView(context: Context, select: Boolean, state: ViewGroupDialogState, actions: ViewGroupDialogActions): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    ViewGroupContent(select, state, actions)
                }
            }
        }
}

@Composable
private fun ViewGroupContent(select: Boolean, state: ViewGroupDialogState, actions: ViewGroupDialogActions) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.menu_controls_groups), style = MaterialTheme.typography.titleLarge)
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            itemsIndexed(state.groups, key = { _, group -> group.id }) { index, group ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(group.name, modifier = Modifier.weight(1f), maxLines = 1)
                        if (select) {
                            Checkbox(
                                checked = group.id in state.selectedIds,
                                onCheckedChange = {
                                    state.setSelected(group, it)
                                    actions.onSelectionChange(group, it)
                                }
                            )
                        } else {
                            IconButton(onClick = { actions.onMove(index, index - 1) }) {
                                Icon(painterResource(R.drawable.ic_baseline_arrow_upward_24), null)
                            }
                            IconButton(onClick = { actions.onMove(index, index + 1) }) {
                                Icon(painterResource(R.drawable.ic_baseline_arrow_downward_24), null)
                            }
                            IconButton(onClick = { actions.onEdit(group) }) {
                                Icon(painterResource(R.drawable.ic_baseline_edit_24), null)
                            }
                            IconButton(onClick = { actions.onDelete(group) }) {
                                Icon(painterResource(R.drawable.ic_baseline_delete_24), null)
                            }
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (!select) {
                TextButton(onClick = actions::onAdd) { Text(context.getString(R.string.menu_control_view_group_add)) }
            } else {
                TextButton(onClick = actions::onDismiss) { Text(context.getString(R.string.dialog_negative)) }
            }
            Button(onClick = actions::onConfirm, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
