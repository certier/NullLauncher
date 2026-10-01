package com.tungsten.fcl.activity

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import com.tungsten.fcllibrary.component.view.FCLEditText

data class ShellActivityComposeViews(
    val root: ComposeView,
    val logWindow: EditText,
    val input: FCLEditText
)

object ShellActivityCompose {
    @JvmStatic
    fun createViews(context: Context): ShellActivityComposeViews {
        val logWindow = AppCompatEditText(context).apply {
            gravity = Gravity.TOP or Gravity.START
            isCursorVisible = false
            setBackgroundColor(Color.TRANSPARENT)
            setTextIsSelectable(true)
            inputType = 0
            setTextColor(Color.WHITE)
        }
        val input = FCLEditText(context).apply {
            maxLines = 1
            setLines(1)
            setTextColor(Color.WHITE)
        }
        val root = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent { ShellContent(logWindow, input) }
        }
        return ShellActivityComposeViews(root, logWindow, input)
    }
}

@Composable
private fun ShellContent(logWindow: EditText, input: FCLEditText) {
    FCLComposeTheme {
        Column(
            Modifier.fillMaxSize()
                .background(ComposeColor.Black)
                .imePadding()
                .padding(10.dp)
        ) {
            AndroidView(
                factory = { logWindow },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            AndroidView(
                factory = { input },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}