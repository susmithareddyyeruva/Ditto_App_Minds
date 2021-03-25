package com.ditto.howto.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController

class MyMediaController : MediaController {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, useFastForward: Boolean) : super(
        context,
        useFastForward
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

     var _isShowing = false

    override fun isShowing(): Boolean {
        return _isShowing
    }

    override fun show() {
        super.show()
        _isShowing = true
        val parent = this.parent as ViewGroup
        parent.visibility = View.VISIBLE
    }

    override fun hide() {
        super.hide()
        _isShowing = false
        val parent = this.parent as ViewGroup
        parent.visibility = View.GONE
    }
}