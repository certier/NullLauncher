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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fclcore.mod.RemoteMod
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

/** Batch download picker with parse progress and preselected matching versions. */
class FavoriteBatchDialog(context: Context) : FCLDialog(context) {
    data class Entry(val title: String, val version: RemoteMod.Version)

    private val entries = mutableStateListOf<Entry>()
    private var parsing by mutableStateOf(true)
    private var skippedCount by mutableStateOf(0)
    private var selectedIndices by mutableStateOf<Set<Int>>(emptySet())
    private var onDownload: ((List<RemoteMod.Version>) -> Unit)? = null

    init {
        setCancelable(true)
        window?.setLayout((400 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    FavoriteBatchContent(
                        entries = entries,
                        parsing = parsing,
                        skippedCount = skippedCount,
                        selectedIndices = selectedIndices,
                        onToggle = ::toggle,
                        onConfirm = ::confirm,
                        onDismiss = ::dismiss
                    )
                }
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun showParsing() {
        parsing = true
        skippedCount = 0
        entries.clear()
        selectedIndices = emptySet()
    }

    fun showResult(
        items: List<Entry>,
        skippedTitles: List<String>,
        callback: (List<RemoteMod.Version>) -> Unit
    ) {
        onDownload = callback
        entries.clear()
        entries.addAll(items)
        selectedIndices = items.indices.toSet()
        skippedCount = skippedTitles.size
        parsing = false
    }

    private fun toggle(index: Int, selected: Boolean) {
        selectedIndices = if (selected) selectedIndices + index else selectedIndices - index
    }

    private fun confirm() {
        val selected = entries.filterIndexed { index, _ -> index in selectedIndices }.map { it.version }
        if (selected.isNotEmpty()) {
            val callback = onDownload
            dismiss()
            callback?.invoke(selected)
        }
    }
}

@Composable
private fun FavoriteBatchContent(
    entries: List<FavoriteBatchDialog.Entry>,
    parsing: Boolean,
    skippedCount: Int,
    selectedIndices: Set<Int>,
    onToggle: (Int, Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.favorite_download_all), style = MaterialTheme.typography.titleLarge)
        if (parsing) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator()
                Text(context.getString(R.string.favorite_batch_parsing))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp).padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(entries) { index, entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = index in selectedIndices,
                            onCheckedChange = { onToggle(index, it) }
                        )
                        Column(Modifier.weight(1f)) {
                            Text(entry.title, maxLines = 1)
                            Text(
                                entry.version.file().filename(),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            if (skippedCount > 0) {
                Text(
                    text = context.getString(R.string.favorite_batch_skipped_note, skippedCount),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) { Text(context.getString(R.string.dialog_negative)) }
            if (!parsing) {
                Button(
                    onClick = onConfirm,
                    enabled = selectedIndices.isNotEmpty(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ) {
                    Text(context.getString(R.string.favorite_batch_download))
                }
            }
        }
    }
}
