package com.tungsten.fcl.game

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

object LoginPromptCompose {
    @JvmStatic
    fun createSkipLoginView(
        context: Context,
        onRetry: Runnable,
        onSkip: Runnable,
        onCancel: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                SkipLoginContent(onRetry, onSkip, onCancel)
            }
        }
    }

    @JvmStatic
    fun createTipReLoginView(
        context: Context,
        onSkip: Runnable,
        onCancel: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                TipReLoginContent(onSkip, onCancel)
            }
        }
    }
}

@Composable
private fun SkipLoginContent(onRetry: Runnable, onSkip: Runnable, onCancel: Runnable) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(context.getString(R.string.account_failed), style = MaterialTheme.typography.titleMedium)
        Text(context.getString(R.string.account_failed_server_disconnected))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onRetry::run) { Text(context.getString(R.string.action_retry)) }
            TextButton(onClick = onSkip::run) { Text(context.getString(R.string.action_skip)) }
            Button(onClick = onCancel::run, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_negative))
            }
        }
    }
}

@Composable
private fun TipReLoginContent(onSkip: Runnable, onCancel: Runnable) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(context.getString(R.string.account_failed), style = MaterialTheme.typography.titleMedium)
        Text(context.getString(R.string.account_failed_expired))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onSkip::run) { Text(context.getString(R.string.action_skip)) }
            Button(onClick = onCancel::run, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
