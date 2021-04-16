package com.ditto.workspace.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView

class WorkspaceEditorView : RelativeLayout {
    private var mImgSource: AppCompatImageView? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    @SuppressLint("Recycle")
    private fun init() {
        //Setup image attributes
        mImgSource = AppCompatImageView(context)
        mImgSource?.setId(imgSrcId)
        mImgSource?.setAdjustViewBounds(true)
        val imgSrcParam = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imgSrcParam.addRule(
            CENTER_IN_PARENT,
            TRUE
        )

        //Add image source
        addView(mImgSource, imgSrcParam)

    }

    /**
     * Source image which you want to edit
     *
     * @return source ImageView
     */
    val source: ImageView?
        get() = mImgSource

    companion object {
        private const val TAG = "WorkspaceEditorView"
        private const val imgSrcId = 1
    }
}