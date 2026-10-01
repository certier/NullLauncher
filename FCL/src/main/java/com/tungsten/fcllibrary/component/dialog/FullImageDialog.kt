package com.tungsten.fcllibrary.component.dialog

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.ui.platform.ComposeView

class FullImageDialog(context: Context) : FCLDialog(context) {
    private val imageView = ImageView(context).apply {
        importantForAccessibility = ImageView.IMPORTANT_FOR_ACCESSIBILITY_NO
        scaleType = ImageView.ScaleType.FIT_CENTER
    }

    init {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCancelable(true)
        setContentView(FullImageDialogCompose.createView(context, imageView, this::dismiss))
    }

    fun getImageView(): ImageView {
        return imageView
    }
}