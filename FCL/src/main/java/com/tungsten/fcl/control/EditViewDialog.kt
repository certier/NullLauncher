package com.tungsten.fcl.control

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.tungsten.fcl.control.data.ControlButtonData
import com.tungsten.fcl.control.data.ControlDirectionData
import com.tungsten.fcl.control.data.CustomControl
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.util.ConvertUtils

/** 游戏内控件编辑对话框：info / event 两页切换，确认 / 复制 / 删除通过回调通知调用方 */
class EditViewDialog(
    context: Context,
    private val cloneView: CustomControl,
    menu: GameMenu?,
    private val callback: Callback,
    cloneable: Boolean,
) : FCLDialog(context) {

    interface Callback {
        fun onPositive(view: CustomControl)

        fun onClone(view: CustomControl)

        fun onDelete() {}
    }

    /** 控件编辑页：info / event 两块布局，提交时给出数据快照 */
    interface Details {
        val infoLayout: View

        val eventLayout: View

        fun getView(): CustomControl
    }

    private val details: Details = if (cloneView.type == CustomControl.ViewType.CONTROL_BUTTON) {
        EditButtonDetails(context, menu, cloneView as ControlButtonData)
    } else {
        EditDirectionDetails(context, menu, cloneView as ControlDirectionData)
    }

    init {
        setCancelable(false)
        // 游戏内悬浮面板：游戏菜单配色 + 与 dialog_background 相同的 10dp inset，
        // 全高窗口靠背景内缩留出与屏幕边缘的间隔
        window?.setBackgroundDrawableResource(R.drawable.bg_game_menu_inset)
        window?.setLayout(ConvertUtils.dip2px(context, 500f), ViewGroup.LayoutParams.MATCH_PARENT)
        val title = context.getString(
            if (cloneView.type == CustomControl.ViewType.CONTROL_BUTTON) R.string.edit_button_title
            else R.string.edit_direction_title
        )
        setContentView(EditViewDialogCompose.createView(
            context,
            title,
            details.infoLayout,
            details.eventLayout,
            cloneable,
            { callback.onClone(cloneView.cloneView()); dismiss() },
            { callback.onDelete(); dismiss() },
            { callback.onPositive(details.getView()); dismiss() },
            this::dismiss
        ))
    }
}
