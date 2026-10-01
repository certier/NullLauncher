package com.tungsten.fcl.ui

import android.content.Context
import android.widget.ListView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class TaskDialogState {
    var title by mutableStateOf("")
        private set
    var speed by mutableStateOf("")
        private set
    var log by mutableStateOf("")
        private set
    var logVisible by mutableStateOf(false)
        private set
    var cancelEnabled by mutableStateOf(false)
        private set

    fun setTitle(value: String) { title = value }
    fun setSpeed(value: String) { speed = value }
    fun setLog(value: String) { log = value; logVisible = value.isNotEmpty() }
    fun clearLog() { log = ""; logVisible = false }
    fun setCancelEnabled(value: Boolean) { cancelEnabled = value }
}

object TaskDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        state: TaskDialogState,
        listView: ListView,
        onCancel: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                TaskDialogContent(state, listView, onCancel)
            }
        }
    }
}

@Composable
private fun TaskDialogContent(state: TaskDialogState, listView: ListView, onCancel: Runnable) {
    val context = LocalContext.current
    val logScroll = rememberScrollState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text(state.title, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium)
        AndroidView(
            factory = { listView },
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)
        )
        if (state.logVisible) {
            HorizontalDivider(Modifier.padding(vertical = 6.dp))
            LaunchedEffect(state.log) {
                logScroll.scrollTo(logScroll.maxValue)
            }
            Text(
                text = state.log,
                modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp)
                    .verticalScroll(logScroll).padding(vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.speed.isNotEmpty()) Text(state.speed, style = MaterialTheme.typography.bodySmall)
            else Text("")
            Button(
                onClick = onCancel::run,
                enabled = state.cancelEnabled,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            ) {
                Text(context.getString(R.string.dialog_negative))
            }
        }
    }
}
