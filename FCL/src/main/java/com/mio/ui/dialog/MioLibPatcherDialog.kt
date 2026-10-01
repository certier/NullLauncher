package com.mio.ui.dialog

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.dialog.FCLDialog

/**
 * MioLibPatcher 功能开关对话框（与 v2 渲染器环境变量对话框同款交互）：
 * ALC10 / Sable Rapier / ASM 后门 三个开关，标题下带说明，确定时全量回传。
 */
class MioLibPatcherDialog(
    context: Context,
    alc10: Boolean,
    sablerapier: Boolean,
    asmBackport: Boolean,
    private val onConfirm: (alc10: Boolean, sablerapier: Boolean, asmBackport: Boolean) -> Unit,
) : FCLDialog(context) {

    init {
        val point = Point()
        window?.windowManager?.defaultDisplay?.getSize(point)
        val params = window?.attributes
        params?.width = (500 * context.resources.displayMetrics.density).toInt()
        val ratio = point.x.toFloat() / point.y.toFloat()
        if (ratio >= 1.5f) {
            params?.height = WindowManager.LayoutParams.MATCH_PARENT
        } else {
            params?.height = point.y * 1 / 2
        }
        window?.attributes = params

        setContentView(MioLibPatcherCompose.createView(
            context,
            alc10,
            sablerapier,
            asmBackport,
            onConfirm,
            this::dismiss
        ))
    }
}