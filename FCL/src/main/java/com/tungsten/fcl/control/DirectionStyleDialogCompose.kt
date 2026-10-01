package com.tungsten.fcl.control

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcl.R
import com.tungsten.fcl.control.data.BaseInfoData
import com.tungsten.fcl.control.data.ControlDirectionStyle
import com.tungsten.fcl.control.view.ControlDirection
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

interface DirectionStyleDialogActions {
    fun onAddStyle()
    fun onEditStyle()
    fun onDeleteStyle(style: ControlDirectionStyle)
    fun onSelectStyle(style: ControlDirectionStyle)
    fun onConfirm()
}

class DirectionStyleDialogState(styles: List<ControlDirectionStyle>, initialStyle: ControlDirectionStyle?) {
    val styles = mutableStateListOf<ControlDirectionStyle>().apply { addAll(styles) }
    var selectedStyle by mutableStateOf(
        initialStyle?.takeIf { style -> styles.any { it.name == style.name } } ?: styles.firstOrNull()
    )
        private set

    fun setSelectedStyle(style: ControlDirectionStyle) {
        selectedStyle = style
    }

    fun refresh(styles: List<ControlDirectionStyle>) {
        this.styles.clear()
        this.styles.addAll(styles)
        if (selectedStyle == null || this.styles.none { it.name == selectedStyle?.name }) {
            selectedStyle = this.styles.firstOrNull()
        }
    }
}

object DirectionStyleDialogCompose {
    @JvmStatic
    fun createView(context: Context, state: DirectionStyleDialogState, select: Boolean, actions: DirectionStyleDialogActions): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    DirectionStyleContent(state, select, actions)
                }
            }
        }
}

@Composable
private fun DirectionStyleContent(state: DirectionStyleDialogState, select: Boolean, actions: DirectionStyleDialogActions) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.menu_controls_direction_style), style = MaterialTheme.typography.titleLarge)
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.styles, key = { it.name }) { style ->
                val isSelected = style.name == state.selectedStyle?.name
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (select) {
                            RadioButton(selected = isSelected, onClick = { actions.onSelectStyle(style) })
                        }
                        AndroidView(
                            factory = { viewContext ->
                                ControlDirection(viewContext).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        (60 * viewContext.resources.displayMetrics.density).toInt(),
                                        (60 * viewContext.resources.displayMetrics.density).toInt()
                                    )
                                    data.style = style
                                    data.baseInfo.sizeType = BaseInfoData.SizeType.ABSOLUTE
                                    data.baseInfo.absoluteWidth = 60
                                    data.baseInfo.absoluteHeight = 60
                                }
                            },
                            update = { view -> view.data.style = style },
                            modifier = Modifier.size(60.dp)
                        )
                        Text(style.name, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), maxLines = 1)
                        if (!select) {
                            IconButton(onClick = { actions.onDeleteStyle(style) }) {
                                Icon(painterResource(R.drawable.ic_baseline_delete_24), contentDescription = context.getString(R.string.button_remove))
                            }
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = actions::onAddStyle) { Text(context.getString(R.string.menu_control_style_add)) }
                if (select) {
                    TextButton(onClick = actions::onEditStyle, enabled = state.selectedStyle != null) {
                        Text(context.getString(R.string.menu_control_style_edit))
                    }
                }
            }
            Button(onClick = actions::onConfirm, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
