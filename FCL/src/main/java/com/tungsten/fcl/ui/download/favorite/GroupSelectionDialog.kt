package com.tungsten.fcl.ui.download.favorite

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.mio.data.FavoriteManager
import com.mio.data.favorite.FavoriteGroupEntity
import com.tungsten.fcl.R
import com.tungsten.fcl.activity.MainActivity
import com.tungsten.fcllibrary.component.dialog.EditDialog
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import kotlinx.coroutines.launch

/** Multi-select favorite groups; group creation is available without closing the picker. */
class GroupSelectionDialog(
    context: Context,
    private val title: String,
    checkedGroupIds: Set<String>,
    private val onConfirm: (List<String>) -> Unit
) : FCLDialog(context) {
    private var groups by mutableStateOf(FavoriteManager.groups.value)
    private var checked by mutableStateOf(checkedGroupIds.toSet())

    init {
        setCancelable(true)
        window?.setLayout((400 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                GroupSelectionContent(
                    title = title,
                    groups = groups,
                    checked = checked,
                    onToggle = ::toggleGroup,
                    onCreate = ::showNewGroupDialog,
                    onConfirm = ::confirmSelection,
                    onDismiss = ::dismiss
                )
            }
        }
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun toggleGroup(id: String, selected: Boolean) {
        checked = if (selected) checked + id else checked - id
    }

    private fun confirmSelection() {
        val selected = groups.filter { it.groupId in checked }.map { it.groupId }
        dismiss()
        onConfirm(selected)
    }

    private fun rebuild() {
        groups = FavoriteManager.groups.value
    }

    private fun showNewGroupDialog() {
        val dialog = EditDialog(context, "") { name ->
            MainActivity.getInstance().lifecycleScope.launch {
                val group = FavoriteManager.createGroup(name)
                checked = checked + group.groupId
                rebuild()
            }
        }
        dialog.setTitle(context.getString(R.string.favorite_group_new))
        dialog.show()
    }
}

@Composable
private fun GroupSelectionContent(
    title: String,
    groups: List<FavoriteGroupEntity>,
    checked: Set<String>,
    onToggle: (String, Boolean) -> Unit,
    onCreate: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (groups.isEmpty()) {
            Text(
                text = context.getString(R.string.favorite_group_select_hint),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(groups, key = { it.groupId }) { group ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = group.groupId in checked,
                            onCheckedChange = { onToggle(group.groupId, it) }
                        )
                        Text(
                            text = group.name,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) { Text(context.getString(R.string.dialog_negative)) }
            TextButton(onClick = onCreate) { Text(context.getString(R.string.favorite_group_new)) }
            Button(onClick = onConfirm, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
