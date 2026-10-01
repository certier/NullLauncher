package com.tungsten.fcl.ui.download.favorite

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.mio.data.FavoriteManager
import com.mio.data.favorite.FavoriteGroupEntity
import com.tungsten.fcl.R
import com.tungsten.fcl.activity.MainActivity
import com.tungsten.fcllibrary.component.dialog.EditDialog
import com.tungsten.fcllibrary.component.dialog.FCLAlertDialog
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import kotlinx.coroutines.launch

/** Manage favorite groups without changing the favorite membership of their items. */
class GroupManageDialog(context: Context) : FCLDialog(context) {
    private var groups by mutableStateOf(FavoriteManager.groups.value)

    init {
        setCancelable(true)
        window?.setLayout((400 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    GroupManageContent(
                        groups = groups,
                        onNew = ::showNewGroupDialog,
                        onRename = ::showRenameDialog,
                        onDelete = ::showDeleteConfirm,
                        onDismiss = ::dismiss
                    )
                }
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        refresh()
    }

    private fun refresh() {
        groups = FavoriteManager.groups.value.toList()
    }

    private fun showNewGroupDialog() {
        val dialog = EditDialog(context, "") { name ->
            MainActivity.getInstance().lifecycleScope.launch {
                FavoriteManager.createGroup(name)
                refresh()
            }
        }
        dialog.setTitle(context.getString(R.string.favorite_group_new))
        dialog.show()
    }

    private fun showRenameDialog(group: FavoriteGroupEntity) {
        val dialog = EditDialog(context, group.name) { newName ->
            MainActivity.getInstance().lifecycleScope.launch {
                if (FavoriteManager.renameGroup(group.groupId, newName)) {
                    refresh()
                } else {
                    Toast.makeText(context, R.string.favorite_group_exists, Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.setTitle(context.getString(R.string.favorite_group_rename))
        dialog.show()
    }

    private fun showDeleteConfirm(group: FavoriteGroupEntity) {
        FCLAlertDialog.Builder(context)
            .setAlertLevel(FCLAlertDialog.AlertLevel.INFO)
            .setMessage(context.getString(R.string.favorite_group_delete_confirm, group.name))
            .setPositiveButton {
                MainActivity.getInstance().lifecycleScope.launch {
                    FavoriteManager.deleteGroup(group.groupId)
                    refresh()
                }
            }
            .setNegativeButton(null)
            .create()
            .show()
    }
}

@Composable
private fun GroupManageContent(
    groups: List<FavoriteGroupEntity>,
    onNew: () -> Unit,
    onRename: (FavoriteGroupEntity) -> Unit,
    onDelete: (FavoriteGroupEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.favorite_group_manage), style = MaterialTheme.typography.titleLarge)
        if (groups.isEmpty()) {
            Text(context.getString(R.string.favorite_group_none), Modifier.padding(vertical = 10.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp).padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groups, key = { it.groupId }) { group ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(group.name, modifier = Modifier.weight(1f), maxLines = 1)
                            IconButton(onClick = { onRename(group) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_baseline_edit_24),
                                    contentDescription = context.getString(R.string.favorite_group_rename)
                                )
                            }
                            IconButton(onClick = { onDelete(group) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_baseline_delete_24),
                                    contentDescription = context.getString(R.string.button_remove)
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) { Text(context.getString(R.string.dialog_negative)) }
            Button(onClick = onNew, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.favorite_group_new))
            }
        }
    }
}
