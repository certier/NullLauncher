package com.tungsten.fcllibrary.crash

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

object CrashReportCompose {
    @JvmStatic
    fun createView(
        context: Context,
        errorDetails: String,
        onRestart: Runnable,
        onClose: Runnable,
        onUpload: Runnable,
        onShare: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                CrashReportContent(errorDetails, onRestart, onClose, onUpload, onShare)
            }
        }
    }
}

@Composable
private fun CrashReportContent(
    errorDetails: String,
    onRestart: Runnable,
    onClose: Runnable,
    onUpload: Runnable,
    onShare: Runnable
) {
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.weight(0.7f).fillMaxHeight()) {
            Text(
                text = androidx.compose.ui.res.stringResource(R.string.crash_reporter_title),
                modifier = Modifier.fillMaxWidth()
                    .padding(12.dp),
                style = MaterialTheme.typography.titleLarge
            )
            SelectionContainer(Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = errorDetails,
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        VerticalDivider()
        Column(
            modifier = Modifier.weight(0.3f).fillMaxHeight().padding(12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            CrashAction(R.string.crash_reporter_restart, onRestart)
            CrashAction(R.string.crash_reporter_close, onClose)
            CrashAction(R.string.crash_reporter_upload, onUpload)
            CrashAction(R.string.crash_reporter_share, onShare)
        }
    }
}

@Composable
private fun CrashAction(label: Int, action: Runnable) {
    val context = LocalContext.current
    Button(
        onClick = action::run,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(context.getString(label))
    }
}