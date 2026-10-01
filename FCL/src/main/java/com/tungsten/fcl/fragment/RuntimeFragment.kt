package com.tungsten.fcl.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.tungsten.fcl.R
import com.tungsten.fcl.activity.SplashActivity
import com.tungsten.fcl.util.RuntimeUtils
import com.tungsten.fclauncher.utils.Architecture
import com.tungsten.fclauncher.utils.FCLPath
import com.tungsten.fcllibrary.component.FCLFragment
import com.tungsten.fcllibrary.component.dialog.FCLAlertDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class RuntimeRow(
    val key: String,
    val label: Int,
    val installed: Boolean,
    val installing: Boolean = false,
    val detail: String? = null
)

class RuntimeFragment : FCLFragment() {
    private val runtimeRows = mutableStateListOf<RuntimeRow>()

    var lwjgl = false
    var cacio = false
    var cacio17 = false
    var java8 = false
    var java25 = false
    var java17 = false
    var java21 = false
    var jna = false

    private var installing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    RuntimeContent(runtimeRows, ::onInstallClick)
                }
            }
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { initState() }
            refreshRows()
            check()
        }
        return composeView
    }

    private fun initState() {
        lwjgl = (activity as SplashActivity).lwjgl
        cacio = (activity as SplashActivity).cacio
        cacio17 = (activity as SplashActivity).cacio17
        java8 = (activity as SplashActivity).java8
        java17 = (activity as SplashActivity).java17
        java21 = (activity as SplashActivity).java21
        java25 = (activity as SplashActivity).java25
        jna = (activity as SplashActivity).jna
    }

    private fun refreshRows() {
        val currentRows = listOf(
            RuntimeRow("lwjgl", R.string.splash_runtime_lwjgl, lwjgl),
            RuntimeRow("cacio", R.string.splash_runtime_cacio, cacio),
            RuntimeRow("cacio17", R.string.splash_runtime_cacio17, cacio17),
            RuntimeRow("java8", R.string.splash_runtime_java8, java8),
            RuntimeRow("java17", R.string.splash_runtime_java17, java17),
            RuntimeRow("java21", R.string.splash_runtime_java21, java21),
            RuntimeRow("java25", R.string.splash_runtime_java25, java25),
            RuntimeRow("jna", R.string.splash_runtime_jna, jna)
        )
        if (runtimeRows.isEmpty()) {
            runtimeRows.addAll(currentRows)
        } else {
            currentRows.forEach { current ->
                val index = runtimeRows.indexOfFirst { it.key == current.key }
                if (index >= 0) runtimeRows[index] = runtimeRows[index].copy(installed = current.installed)
            }
        }
    }

    private fun updateRow(key: String, update: (RuntimeRow) -> RuntimeRow) {
        val index = runtimeRows.indexOfFirst { it.key == key }
        if (index >= 0) runtimeRows[index] = update(runtimeRows[index])
    }

    private val isLatest: Boolean
        get() = lwjgl && cacio && cacio17 && java8 && java25 && java17 && java21 && jna

    private fun check() {
        if (isLatest) {
            view?.visibility = View.GONE
            (activity as SplashActivity).enterLauncher()
        }
    }

    private fun install() {
        if (installing) return
        installing = true

        if (!lwjgl) launchInstall("lwjgl", { lwjgl = true }) {
            RuntimeUtils.install(context, FCLPath.LWJGL_DIR, "app_runtime/lwjgl", it)
        }
        if (!cacio) launchInstall("cacio", { cacio = true }) {
            RuntimeUtils.install(context, FCLPath.CACIOCAVALLO_8_DIR, "app_runtime/caciocavallo", it)
        }
        if (!cacio17) launchInstall("cacio17", { cacio17 = true }) {
            RuntimeUtils.install(context, FCLPath.CACIOCAVALLO_17_DIR, "app_runtime/caciocavallo17", it)
        }
        if (!java8) launchInstall("java8", { java8 = true }) {
            RuntimeUtils.installJava(context, FCLPath.JAVA_8_PATH, "app_runtime/java/jre8", it)
        }
        if (!java17) launchInstall("java17", { java17 = true }) {
            RuntimeUtils.installJava(context, FCLPath.JAVA_17_PATH, "app_runtime/java/jre17", it)
        }
        if (!java21) launchInstall("java21", { java21 = true }) {
            RuntimeUtils.installJava(context, FCLPath.JAVA_21_PATH, "app_runtime/java/jre21", it)
        }
        if (!java25) launchInstall("java25", { java25 = true }) {
            RuntimeUtils.installJava(context, FCLPath.JAVA_25_PATH, "app_runtime/java/jre25", it)
        }
        if (!jna) launchInstall("jna", { jna = true }) {
            RuntimeUtils.installJna(context, FCLPath.JNA_PATH, "app_runtime/jna", it)
        }
    }

    private fun launchInstall(
        key: String,
        markDone: () -> Unit,
        block: (RuntimeUtils.InstallListener) -> Unit
    ) {
        updateRow(key) { it.copy(installing = true, detail = null) }
        val listener = createListener(key)
        lifecycleScope.launch {
            val error = withContext(Dispatchers.IO) {
                runCatching { block(listener) }.exceptionOrNull()
            }
            updateRow(key) { it.copy(installing = false, detail = null) }
            if (error != null) {
                showErrorDialog(error.toString())
            } else {
                markDone()
            }
            refreshRows()
            check()
        }
    }

    private fun createListener(key: String): RuntimeUtils.InstallListener {
        val mainHandler = Handler(Looper.getMainLooper())
        var lastUpdateTime = 0L
        fun post(text: String, force: Boolean = false) {
            val now = SystemClock.elapsedRealtime()
            if (!force && now - lastUpdateTime < DETAIL_UPDATE_INTERVAL_MS) return
            lastUpdateTime = now
            mainHandler.post {
                if (isAdded) updateRow(key) { it.copy(detail = text) }
            }
        }
        return object : RuntimeUtils.InstallListener {
            override fun onUpdate(detailText: String) {
                post(detailText)
            }

            override fun onStage(resId: Int) {
                post(getString(resId), force = true)
            }
        }
    }

    private fun onInstallClick() {
        val deviceArch = Architecture.archAsString(Architecture.getDeviceArchitecture())
        if (!isJavaArchSupported(deviceArch)) {
            showErrorDialog(
                getString(
                    R.string.missing_runtime_arch_files,
                    deviceArch,
                    "FCL-release-x.x.x.x-$deviceArch.apk",
                    "FCL-release-x.x.x.x-all.apk"
                )
            )
            return
        }
        install()
    }

    private fun isJavaArchSupported(arch: String): Boolean {
        try {
            val javaDirs = listOf("jre8", "jre17", "jre21", "jre25")
            val assetManager = requireContext().assets
            var supportedCount = 0
            for (javaDir in javaDirs) {
                val dirPath = "app_runtime/java/$javaDir"
                val files = assetManager.list(dirPath)
                if (files != null && files.contains("bin-$arch.tar.xz")) supportedCount++
            }
            return supportedCount > 0
        } catch (e: Exception) {
            showErrorDialog(e.toString())
            return false
        }
    }

    private fun showErrorDialog(message: String) {
        installing = false
        lifecycleScope.launch(Dispatchers.Main) {
            FCLAlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton { }
                .create()
                .show()
        }
    }

    companion object {
        private const val DETAIL_UPDATE_INTERVAL_MS = 50L
    }
}

@Composable
private fun RuntimeContent(rows: List<RuntimeRow>, onInstall: () -> Unit) {
    val context = LocalContext.current
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.splash_title),
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )
            Row(Modifier.weight(1f).fillMaxWidth()) {
                LazyColumn(Modifier.weight(0.7f).fillMaxHeight()) {
                    items(rows, key = { it.key }) { row ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(row.label), modifier = Modifier.weight(1f))
                                if (row.installing) {
                                    CircularProgressIndicator(Modifier.size(24.dp))
                                } else {
                                    Icon(
                                        painter = painterResource(
                                            if (row.installed) R.drawable.ic_baseline_done_24
                                            else R.drawable.ic_baseline_update_24
                                        ),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            row.detail?.let { detail ->
                                Text(
                                    text = detail,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp, bottom = 6.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
                Box(
                    Modifier.width(1.dp).fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Column(
                    modifier = Modifier.weight(0.3f).fillMaxHeight().padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = context.getString(R.string.splash_runtime_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = onInstall,
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                    ) {
                        Text(context.getString(R.string.splash_runtime_install))
                    }
                }
            }
        }
    }
}
