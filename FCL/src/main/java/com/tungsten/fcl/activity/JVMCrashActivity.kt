package com.tungsten.fcl.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.util.TypedValue
import android.view.WindowManager
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.mio.util.showErrorDialog
import com.tungsten.fcl.R
import com.tungsten.fclcore.util.Logging
import com.tungsten.fclcore.util.StringUtils
import com.tungsten.fclcore.util.io.FileUtils
import com.tungsten.fcllibrary.component.FCLActivity
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import com.tungsten.fcllibrary.component.theme.ThemeEngine
import com.tungsten.fcllibrary.util.findFatalErrorLogPath
import com.tungsten.fcllibrary.util.shareLogFile
import com.tungsten.fcllibrary.util.uploadLog
import java.io.File
import java.io.IOException
import java.util.logging.Level
import kotlin.system.exitProcess

class JVMCrashActivity : FCLActivity() {
    private var exitCode = 0
    private lateinit var logPath: String
    private lateinit var composeRoot: ComposeView
    private var crashTitle by mutableStateOf("")
    private var errorText by mutableStateOf("")
    private var fatalErrorLogPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        composeRoot = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    CrashReportContent(
                        title = crashTitle,
                        error = errorText,
                        onRestart = ::restartLauncher,
                        onClose = ::closeApp,
                        onUpload = ::uploadCrashLog,
                        onShare = ::shareCrashLog
                    )
                }
            }
        }
        setContentView(composeRoot)
        if (!getSharedPreferences("launcher", MODE_PRIVATE).getBoolean("allowScreenshots", false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        ThemeEngine.registerEvent(composeRoot) {
            composeRoot.setBackgroundColor(resolveWindowBackgroundColor())
        }

        val extras = requireNotNull(intent.extras)
        val game = extras.getBoolean("isGame")
        exitCode = extras.getInt("exitCode")
        logPath = extras.getString("logPath").toString()
        crashTitle = if (game) {
            getString(R.string.game_crash_title) + getString(R.string.game_crash_title_add)
        } else {
            getString(R.string.jar_executor_crash_title)
        }

        try {
            init()
        } catch (e: IOException) {
            Logging.LOG.log(Level.WARNING, "Failed to read log file", e)
            errorText = e.message.orEmpty()
        }
    }

    private fun resolveWindowBackgroundColor(): Int {
        val outValue = TypedValue()
        if (theme.resolveAttribute(android.R.attr.windowBackground, outValue, true) &&
            outValue.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
            outValue.type <= TypedValue.TYPE_LAST_COLOR_INT
        ) {
            return outValue.data
        }
        return if (ThemeEngine.isNightMode(this)) Color.BLACK else Color.WHITE
    }

    @Throws(IOException::class)
    private fun init() {
        val log = readLog()
        val fabricError = findFabricIncompatibleModsError(log)
        fatalErrorLogPath = findFatalErrorLogPath(log)
        fabricError?.let { showErrorDialog(this, it) }
        errorText = log.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
            .joinToString(separator = "\n", postfix = "\n")
    }

    private fun restartLauncher() {
        val restartIntent = Intent(this, SplashActivity::class.java)
        restartIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
        if (restartIntent.component != null) {
            restartIntent.setAction(Intent.ACTION_MAIN)
            restartIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        }
        finish()
        startActivity(restartIntent)
        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    private fun closeApp() {
        finish()
        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    private fun uploadCrashLog() {
        try {
            var log = readLog()
            fatalErrorLogPath?.let { log += "\n${readLog(it)}" }
            uploadLog(this, log)
        } catch (e: IOException) {
            showErrorDialog(this, R.string.upload_failed, e.message)
        }
    }

    private fun shareCrashLog() {
        shareLogFile(this, File(logPath))
    }

    @Throws(IOException::class)
    private fun readLog(path: String = logPath): String {
        if (File(path).length() < 8 * 1024 * 1024) {
            return FileUtils.readText(File(path))
        }
        throw IOException("Log file is too large, please check the log file manually.")
    }

    fun findFabricIncompatibleModsError(text: String): String? {
        val pattern = Regex(
            """start net\.fabricmc\.loader\.impl\.gui\.FabricGuiEntry\.displayError(.*?)end net\.fabricmc\.loader\.impl\.gui\.FabricGuiEntry\.displayError""",
            RegexOption.DOT_MATCHES_ALL
        )
        return pattern.find(text)?.groupValues?.get(1)
    }

    companion object {
        @JvmStatic
        fun startCrashActivity(game: Boolean, context: Context, exitCode: Int, logPath: String?) {
            val crashIntent = Intent(context, JVMCrashActivity::class.java)
            val bundle = Bundle().apply {
                putBoolean("isGame", game)
                putInt("exitCode", exitCode)
                putString("logPath", logPath)
            }
            crashIntent.putExtras(bundle)
            crashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(crashIntent)
        }
    }
}

@Composable
private fun CrashReportContent(
    title: String,
    error: String,
    onRestart: () -> Unit,
    onClose: () -> Unit,
    onUpload: () -> Unit,
    onShare: () -> Unit
) {
    Row(Modifier.fillMaxSize()) {
        Column(Modifier.weight(0.7f).fillMaxHeight()) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                style = MaterialTheme.typography.titleLarge
            )
            SelectionContainer(Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = error,
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
            CrashActionButton(R.string.crash_reporter_restart, onRestart)
            CrashActionButton(R.string.crash_reporter_close, onClose)
            CrashActionButton(R.string.crash_reporter_upload, onUpload)
            CrashActionButton(R.string.crash_reporter_share, onShare)
        }
    }
}

@Composable
private fun CrashActionButton(label: Int, onClick: () -> Unit) {
    val context = LocalContext.current
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(context.getString(label))
    }
}
