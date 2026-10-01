package com.tungsten.fcl.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.tungsten.fcl.R
import com.tungsten.fcl.activity.SplashActivity
import com.tungsten.fcllibrary.component.FCLFragment
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EulaFragment : FCLFragment() {
    private var eulaText by mutableStateOf<String?>(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            FCLComposeTheme {
                EulaContent(eulaText, ::continueToRuntime)
            }
        }
    }.also {
        lifecycleScope.launch {
            eulaText = withContext(Dispatchers.IO) {
                runCatching {
                    requireContext().assets.open("eula.txt").bufferedReader().use { it.readText() }
                }.getOrElse { getString(R.string.splash_eula_error) }
            }
        }
    }

    private fun continueToRuntime() {
        requireContext().getSharedPreferences("launcher", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("isFirstLaunch", false)
            .apply()
        (requireActivity() as SplashActivity).start()
    }
}

@Composable
private fun EulaContent(eula: String?, onContinue: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize()) {
        Text(
            text = context.getString(R.string.splash_title),
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(10.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
        Row(Modifier.weight(1f).fillMaxWidth()) {
            Box(Modifier.weight(0.7f).fillMaxHeight()) {
                if (eula == null) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else {
                    Text(
                        text = eula,
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(10.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Box(Modifier.weight(0.3f).fillMaxHeight()) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(10.dp)
                ) {
                    Text(
                        text = context.getString(R.string.splash_eula_next),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
