package com.tungsten.fcl.control

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tungsten.fcl.R
import com.tungsten.fcl.control.data.ControlButtonStyle
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

interface ButtonStyleDialogActions {
    fun onAddStyle()
    fun onEditStyle()
    fun onDeleteStyle(style: ControlButtonStyle)
    fun onSelectStyle(style: ControlButtonStyle)
    fun onConfirm()
}

class ButtonStyleDialogState(styles: List<ControlButtonStyle>, initialStyle: ControlButtonStyle?) {
    val styles = mutableStateListOf<ControlButtonStyle>().apply { addAll(styles) }
    var selectedStyle by mutableStateOf(
        initialStyle?.takeIf { item -> styles.any { it.name == item.name } } ?: styles.firstOrNull()
    )
        private set

    fun setSelectedStyle(style: ControlButtonStyle) {
        selectedStyle = style
    }

    fun refresh(styles: List<ControlButtonStyle>) {
        this.styles.clear()
        this.styles.addAll(styles)
        if (selectedStyle == null || this.styles.none { it.name == selectedStyle?.name }) {
            selectedStyle = this.styles.firstOrNull()
        }
    }
}

object ButtonStyleDialogCompose {
    @JvmStatic
    fun createView(context: Context, state: ButtonStyleDialogState, select: Boolean, actions: ButtonStyleDialogActions): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    ButtonStyleContent(state, select, actions)
                }
            }
        }
}

@Composable
private fun ButtonStyleContent(state: ButtonStyleDialogState, select: Boolean, actions: ButtonStyleDialogActions) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text(context.getString(R.string.menu_controls_button_style), style = MaterialTheme.typography.titleLarge)
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
                            RadioButton(
                                selected = isSelected,
                                onClick = { actions.onSelectStyle(style) }
                            )
                        }
                        StylePreview(style)
                        Text(
                            text = style.name,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            maxLines = 1
                        )
                        IconButton(onClick = { actions.onDeleteStyle(style) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_delete_24),
                                contentDescription = context.getString(R.string.button_remove)
                            )
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = actions::onAddStyle) {
                    Text(context.getString(R.string.menu_control_style_add))
                }
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

@Composable
private fun StylePreview(style: ControlButtonStyle) {
    val interaction = androidx.compose.runtime.remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Surface(
        onClick = {},
        modifier = Modifier.size(50.dp),
        shape = RoundedCornerShape(((if (pressed) style.cornerRadiusPressed else style.cornerRadius) / 10f).dp),
        color = Color(if (pressed) style.fillColorPressed else style.fillColor),
        contentColor = Color(if (pressed) style.textColorPressed else style.textColor),
        border = BorderStroke(
            ((if (pressed) style.strokeWidthPressed else style.strokeWidth) / 10f).dp,
            Color(if (pressed) style.strokeColorPressed else style.strokeColor)
        ),
        interactionSource = interaction
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "S",
                fontSize = (if (pressed) style.textSizePressed else style.textSize).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
