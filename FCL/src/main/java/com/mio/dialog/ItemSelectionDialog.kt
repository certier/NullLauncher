package com.mio.dialog

import android.content.Context
import android.graphics.Paint
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class ItemSelectionDialog(
    context: Context,
    title: String,
    items: List<String>,
    small: Boolean,
    selectedIndex: Int = -1,
    callback: (Int, String) -> Unit
) : FCLDialog(context) {
    init {
        val metrics = context.resources.displayMetrics
        val density = metrics.density
        val paint = Paint().apply { textSize = 16 * density }
        val longestItem = items.maxOfOrNull(paint::measureText) ?: 0f
        val titleWidth = paint.measureText(title)
        val maxWidth = minOf((560 * density).toInt(), metrics.widthPixels - (48 * density).toInt())
        val width = maxOf((320 * density).toInt(), (maxOf(longestItem, titleWidth) + 48 * density).toInt())
            .coerceAtMost(maxWidth)
        val headTailHeight = (124 * density).toInt()
        val maxWindowHeight = (metrics.heightPixels * if (small) 0.5f else 0.9f).toInt()
        val listMaxHeight = (maxWindowHeight - headTailHeight).coerceAtLeast(0)
        setContentView(ItemSelectionCompose.createView(context, title, items, selectedIndex, listMaxHeight, callback, this::dismiss),
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }
}

private object ItemSelectionCompose {
    fun createView(
        title: String,
        items: List<String>,
        selectedIndex: Int,
        listMaxHeight: Int,
        callback: (Int, String) -> Unit,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    ItemSelectionContent(title, items, selectedIndex, listMaxHeight, callback, onDismiss)
                }
            }
        }
}

@Composable
private fun ItemSelectionContent(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    listMaxHeight: Int,
    callback: (Int, String) -> Unit,
    onDismiss: Runnable
) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    Column(Modifier.fillMaxWidth().padding(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = (listMaxHeight / density).dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(items) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { callback(index, item) },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == selectedIndex) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        color = if (index == selectedIndex) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        TextButton(onClick = onDismiss::run, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
            Text(context.getString(R.string.dialog_negative))
        }
    }
}
