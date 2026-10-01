package com.tungsten.fcl.control

import android.content.Context
import android.graphics.Point
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.tungsten.fcl.setting.Controller
import com.tungsten.fcl.setting.Controllers
import com.tungsten.fcllibrary.component.dialog.FCLDialog

/** Controller picker with a selected marker for the current controller. */
class SelectControllerDialog(
    context: Context,
    currentId: String,
    private val callback: (Controller) -> Unit
) : FCLDialog(context) {
    init {
        val point = Point()
        window?.windowManager?.defaultDisplay?.getSize(point)
        val params = window?.attributes
        params?.width = (500 * context.resources.displayMetrics.density).toInt()
        params?.height = if (point.x.toFloat() / point.y.toFloat() >= 1.5f) {
            WindowManager.LayoutParams.MATCH_PARENT
        } else {
            point.y / 2
        }
        window?.attributes = params
        setContentView(ControllerSelectionCompose.createView(
            context,
            Controllers.getControllers(),
            currentId,
            { controller ->
                callback(controller)
                dismiss()
            },
            this::dismiss
        ), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}

private object ControllerSelectionCompose {
    fun createView(
        context: Context,
        controllers: List<Controller>,
        currentId: String,
        onSelect: (Controller) -> Unit,
        onCancel: () -> Unit
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            com.tungsten.fcllibrary.component.theme.FCLComposeTheme {
                ControllerSelectionContent(controllers, currentId, onSelect, onCancel)
            }
        }
    }
}

@Composable
private fun ControllerSelectionContent(
    controllers: List<Controller>,
    currentId: String,
    onSelect: (Controller) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.control_select), style = MaterialTheme.typography.titleLarge)
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(controllers, key = { it.id }) { controller ->
                val selected = controller.id == currentId
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(controller) },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(controller.name, maxLines = 2, style = MaterialTheme.typography.titleSmall)
                            Text(controller.version, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                            Text(controller.description, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                        }
                        if (selected) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_done_24),
                                contentDescription = null,
                                modifier = Modifier.padding(start = 8.dp).size(24.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
        TextButton(onClick = onCancel, modifier = Modifier.align(Alignment.End)) {
            Text(context.getString(R.string.button_cancel))
        }
    }
}
