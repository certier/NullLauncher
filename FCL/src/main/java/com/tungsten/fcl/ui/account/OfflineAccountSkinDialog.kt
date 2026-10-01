package com.tungsten.fcl.ui.account

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.mio.skin.SkinRenderer
import com.mio.skin.SkinViewer
import com.mio.util.getScreenHeight
import com.mio.util.getScreenWidth
import com.tungsten.fcl.R
import com.tungsten.fcl.activity.MainActivity
import com.tungsten.fcl.game.TexturesLoader
import com.tungsten.fclauncher.utils.FCLPath
import com.tungsten.fclcore.auth.offline.OfflineAccount
import com.tungsten.fclcore.auth.offline.Skin
import com.tungsten.fclcore.auth.offline.Skin.LoadedSkin
import com.tungsten.fclcore.auth.yggdrasil.TextureModel
import com.tungsten.fclcore.task.Schedulers
import com.tungsten.fclcore.util.Logging
import com.tungsten.fclcore.util.StringUtils
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import com.tungsten.fcllibrary.util.LocaleUtils
import java.io.File
import java.util.logging.Level

class OfflineAccountSkinDialog(context: Context, private val accountListItem: AccountListItem) : FCLDialog(context) {
    private val account: OfflineAccount = accountListItem.account as OfflineAccount
    private val renderer = SkinRenderer(context)
    private var model by mutableStateOf(TextureModel.STEVE)
    private var skinPath by mutableStateOf<String?>(null)
    private var capePath by mutableStateOf<String?>(null)

    init {
        setCancelable(false)
        val skin = account.skin
        if (skin == null) {
            model = TextureModel.detectUUID(account.uuid)
        } else {
            model = if (skin.type() == Skin.Type.ALEX) TextureModel.ALEX else skin.textureModel()
            skinPath = skin.localSkinPath()
            capePath = skin.localCapePath()
        }
        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()
        val height = if (screenHeight * 2 < screenWidth) WindowManager.LayoutParams.MATCH_PARENT else screenHeight * 2 / 3
        window?.setLayout(screenWidth * 2 / 3, height)

        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    OfflineSkinContent(
                        renderer = renderer,
                        model = model,
                        skinPath = skinPath,
                        capePath = capePath,
                        onModelChange = {
                            model = it
                            refreshSkin()
                        },
                        onChooseSkin = ::chooseSkin,
                        onResetSkin = { skinPath = null; refreshSkin() },
                        onChooseCape = ::chooseCape,
                        onResetCape = { capePath = null; refreshSkin() },
                        onSave = ::saveSkin,
                        onCancel = ::dismiss
                    )
                }
            }
        }
        setContentView(composeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        refreshSkin()
    }

    private fun chooseSkin() {
        MainActivity.getInstance().fileLauncher.launchSingleSelection(null, listOf(".png")) { files ->
            skinPath = files?.firstOrNull()?.toFile(context, File(FCLPath.CACHE_DIR))?.absolutePath ?: return@launchSingleSelection
            refreshSkin()
        }
    }

    private fun chooseCape() {
        MainActivity.getInstance().fileLauncher.launchSingleSelection(null, listOf(".png")) { files ->
            capePath = files?.firstOrNull()?.toFile(context, File(FCLPath.CACHE_DIR))?.absolutePath ?: return@launchSingleSelection
            refreshSkin()
        }
    }

    private fun refreshSkin() {
        skin.load().whenComplete(Schedulers.androidUIThread()) { result: LoadedSkin?, exception: Exception? ->
            if (exception != null) {
                Logging.LOG.log(Level.WARNING, "Failed to load skin", exception)
                Toast.makeText(context, context.getString(R.string.message_failed), Toast.LENGTH_SHORT).show()
            } else {
                val slim = model == TextureModel.ALEX
                if (result == null || result.skin() == null && result.cape() == null) {
                    renderer.updateTexture(TexturesLoader.getDefaultSkin(model).image(), null, slim)
                    return@whenComplete
                }
                renderer.updateTexture(
                    result.skin()?.image ?: TexturesLoader.getDefaultSkin(model).image(),
                    result.cape()?.image,
                    slim
                )
            }
        }.start()
    }

    private val skin: Skin
        get() {
            val hasSkin = StringUtils.isNotBlank(skinPath)
            val hasCape = StringUtils.isNotBlank(capePath)
            return if (hasSkin || hasCape) {
                Skin(Skin.Type.LOCAL_FILE, model, if (hasSkin) skinPath else null, if (hasCape) capePath else null)
            } else {
                Skin(Skin.Type.DEFAULT, model, null, null)
            }
        }

    private fun saveSkin() {
        skinPath = commitSkinFile(skinPath, "${account.uuid}.png")
        capePath = commitSkinFile(capePath, "${account.uuid}_cape.png")
        account.setSkin(skin)
        accountListItem.refreshSkinBinding()
        dismiss()
    }

    private fun commitSkinFile(path: String?, fileName: String): String? {
        path ?: return null
        val source = File(path)
        val destination = File(FCLPath.SKIN_DIR, fileName)
        if (source.canonicalPath == destination.canonicalPath) return destination.absolutePath
        return runCatching { source.copyTo(destination, overwrite = true).absolutePath }.getOrElse { path }
    }
}

@Composable
private fun OfflineSkinContent(
    renderer: SkinRenderer,
    model: TextureModel,
    skinPath: String?,
    capePath: String?,
    onModelChange: (TextureModel) -> Unit,
    onChooseSkin: () -> Unit,
    onResetSkin: () -> Unit,
    onChooseCape: () -> Unit,
    onResetCape: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    Row(Modifier.fillMaxSize().padding(12.dp)) {
        AndroidView(
            factory = { viewContext ->
                SkinViewer(viewContext).apply {
                    setRenderer(renderer, 5f)
                    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(view: View) = onResume()
                        override fun onViewDetachedFromWindow(view: View) = onPause()
                    })
                }
            },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()).padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(context.getString(R.string.account_skin), style = MaterialTheme.typography.titleLarge)
            Text(context.getString(R.string.account_skin_model), style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = model == TextureModel.STEVE, onClick = { onModelChange(TextureModel.STEVE) })
                Text(context.getString(R.string.account_skin_model_classic), modifier = Modifier.weight(1f))
                RadioButton(selected = model == TextureModel.ALEX, onClick = { onModelChange(TextureModel.ALEX) })
                Text(context.getString(R.string.account_skin_model_slim))
            }
            SkinPathRow(context.getString(R.string.account_skin), skinPath, onChooseSkin, onResetSkin)
            SkinPathRow(context.getString(R.string.account_cape), capePath, onChooseCape, onResetCape)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onCancel) { Text(context.getString(R.string.dialog_negative)) }
                Button(onClick = onSave, shape = RoundedCornerShape(4.dp)) { Text(context.getString(R.string.dialog_positive)) }
            }
        }
    }
}

@Composable
private fun SkinPathRow(label: String, path: String?, onChoose: () -> Unit, onReset: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            IconButton(onClick = onChoose) {
                Icon(painterResource(R.drawable.ic_baseline_edit_24), contentDescription = label)
            }
            IconButton(onClick = onReset) {
                Icon(painterResource(R.drawable.ic_baseline_refresh_24), contentDescription = context.getString(R.string.button_reset))
            }
        }
        Text(path.orEmpty(), maxLines = 1, style = MaterialTheme.typography.bodySmall)
    }
}
