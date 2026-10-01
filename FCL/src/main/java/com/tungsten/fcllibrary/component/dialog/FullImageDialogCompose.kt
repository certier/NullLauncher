package com.tungsten.fcllibrary.component.dialog

import android.content.Context
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

object FullImageDialogCompose {
    @JvmStatic
    fun createView(context: Context, imageView: ImageView, onDismiss: Runnable): ComposeView =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    FullImageContent(imageView, onDismiss)
                }
            }
        }
}

@Composable
private fun FullImageContent(imageView: ImageView, onDismiss: Runnable) {
    Box(Modifier.fillMaxSize()) {
        AndroidView(factory = { imageView }, modifier = Modifier.fillMaxSize())
        IconButton(
            onClick = onDismiss::run,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_close_24),
                contentDescription = androidx.compose.ui.res.stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}