package com.tungsten.fcl.control

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcl.control.data.QuickInputTexts
import com.tungsten.fclcore.util.StringUtils
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class AddInputTextDialog(context: Context, private val callback: Callback) : FCLDialog(context) {
    private var inputText by mutableStateOf("")
    private var remarks by mutableStateOf("")

    fun interface Callback {
        fun onTextAdd()
    }

    init {
        setCancelable(false)
        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    AddInputTextContent(
                        inputText = inputText,
                        remarks = remarks,
                        onInputTextChange = { inputText = it },
                        onRemarksChange = { remarks = it },
                        onSave = ::save,
                        onCancel = ::dismiss
                    )
                }
            }
        }
        setContentView(composeView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        window?.setLayout(
            (400 * context.resources.displayMetrics.density).toInt(),
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun save() {
        when {
            StringUtils.isBlank(inputText) -> Toast.makeText(
                context,
                context.getString(R.string.quick_input_empty),
                Toast.LENGTH_SHORT
            ).show()

            QuickInputTexts.getInputTexts().contains(inputText) -> Toast.makeText(
                context,
                context.getString(R.string.quick_input_exist),
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val storedText = if (StringUtils.isNotBlank(remarks)) "$remarks&*&$inputText" else inputText
                QuickInputTexts.addInputText(storedText)
                callback.onTextAdd()
                dismiss()
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AddInputTextContent(
    inputText: String,
    remarks: String,
    onInputTextChange: (String) -> Unit,
    onRemarksChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    Card(shape = RoundedCornerShape(4.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(context.getString(R.string.quick_input_add), style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                label = { Text(context.getString(R.string.quick_input_text)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = remarks,
                onValueChange = onRemarksChange,
                label = { Text(context.getString(R.string.quick_input_remarks)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onCancel) {
                    Text(context.getString(R.string.dialog_negative))
                }
                Button(onClick = onSave, shape = RoundedCornerShape(4.dp)) {
                    Text(context.getString(R.string.dialog_positive))
                }
            }
        }
    }
}