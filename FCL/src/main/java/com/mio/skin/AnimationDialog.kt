package com.mio.skin

import android.content.Context
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.mio.util.getScreenWidth
import com.tungsten.fcllibrary.component.dialog.FCLDialog

/** 动画选中回调（SAM 接口，便于 Java 侧 lambda 调用），参数为烘焙 clip 名 */
fun interface OnAnimationSelectedListener {
    fun onSelected(clipId: String)
}

/** 3D 皮肤层开关回调（SAM 接口，便于 Java 侧 lambda 调用），参数为新的开关状态 */
fun interface OnSolidLayerToggledListener {
    fun onToggled(enabled: Boolean)
}

/** 身体与腿部分离开关回调（SAM 接口，便于 Java 侧 lambda 调用），参数为新的开关状态 */
fun interface OnBodySeparationToggledListener {
    fun onToggled(separated: Boolean)
}

/**
 * 皮肤模型设置弹窗：顶部 3D 皮肤层与身体腿部分离两个开关，下方列出全部支持的动画并标记当前项；
 * 点动画条目即切换并关闭，开关切换即时生效且保持弹窗打开。
 */
class AnimationDialog(
    context: Context,
    private val currentId: String?,
    private val solidLayerEnabled: Boolean,
    private val upperBodySeparated: Boolean,
    private val onSelected: OnAnimationSelectedListener,
    private val onSolidLayerToggled: OnSolidLayerToggledListener,
    private val onBodySeparationToggled: OnBodySeparationToggledListener
) : FCLDialog(context) {

    init {
        setCancelable(true)
        setContentView(AnimationDialogCompose.createView(
            context,
            currentId,
            solidLayerEnabled,
            upperBodySeparated,
            SkinAnimations.entries,
            onSelected,
            onSolidLayerToggled,
            onBodySeparationToggled,
            this::dismiss
        ))
    }

    override fun show() {
        window?.setLayout(getScreenWidth() / 2, WindowManager.LayoutParams.WRAP_CONTENT)
        super.show()
    }
}
