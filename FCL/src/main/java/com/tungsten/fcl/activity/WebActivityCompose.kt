package com.tungsten.fcl.activity

import android.content.Context
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import java.util.function.Consumer

class WebActivityComposeState {
    var loading by mutableStateOf(true)
        private set

    fun setLoading(value: Boolean) {
        loading = value
    }
}

object WebActivityCompose {
    @JvmStatic
    fun createView(
        context: Context,
        url: String,
        state: WebActivityComposeState,
        onWebViewCreated: Consumer<WebView>
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                WebActivityContent(url, state.loading, onWebViewCreated)
            }
        }
    }
}

@Composable
private fun WebActivityContent(
    url: String,
    loading: Boolean,
    onWebViewCreated: Consumer<WebView>
) {
    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).also { webView ->
                    onWebViewCreated.accept(webView)
                    webView.loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (loading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}