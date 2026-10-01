package com.tungsten.fcllibrary.component.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import android.view.Gravity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.view.FCLEditText
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.util.function.Consumer

class FullEditDialog(
    context: Context,
    private val canBeEmpty: Boolean = false,
    private val callback: Consumer<String>
) : FCLDialog(context) {
    private var titleText by mutableStateOf(context.getString(R.string.edit))
    val binding: FullEditDialogViews

    init {
        setCancelable(false)
        val editText = FCLEditText(context).apply {
            setSingleLine(false)
            gravity = Gravity.TOP or Gravity.START
            imeOptions = android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI
            textSize = 14f
        }
        binding = FullEditDialogViews(editText)
        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    FullEditDialogContent(
                        title = titleText,
                        editText = editText,
                        onConfirm = {
                            val value = editText.text.toString()
                            if (canBeEmpty || value.trim { it <= ' ' }.isNotEmpty()) {
                                callback.accept(value)
                                dismiss()
                            }
                        },
                        onCancel = ::dismiss
                    )
                }
            }
        }
        setContentView(composeView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        window?.setLayout((400 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.MATCH_PARENT)
    }

    override fun setTitle(titleId: Int) {
        titleText = context.getString(titleId)
    }

    override fun setTitle(title: CharSequence?) {
        titleText = title?.toString().orEmpty()
    }

    @SuppressLint("SetTextI18n")
    fun appendTitle(title: String) {
        titleText = "$titleText $title"
    }

    fun getEditText(): FCLEditText {
        return binding.editText
    }
}

class FullEditDialogViews(val editText: FCLEditText)

@Composable
private fun FullEditDialogContent(
    title: String,
    editText: FCLEditText,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        AndroidView(factory = { editText }, modifier = Modifier.weight(1f).fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onCancel) {
                Text(context.getString(R.string.dialog_negative))
            }
            Button(onClick = onConfirm, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
