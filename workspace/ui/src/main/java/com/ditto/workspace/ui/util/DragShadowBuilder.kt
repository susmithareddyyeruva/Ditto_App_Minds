package com.ditto.workspace.ui.util

import android.graphics.Canvas
import android.graphics.Point
import android.view.View

class DragShadowBuilder(v: View) : View.DragShadowBuilder(v) {
    var rotationRad = Math.toRadians(v.rotation.toDouble())
    val w :Float = v.width * v.scaleX
    val h :Float = v.height * v.scaleY
    var s :Float = Math.abs(Math.sin(rotationRad)).toFloat()
    var c :Float = Math.abs(Math.cos(rotationRad)).toFloat()
    val width :Float = w * c + h * s
    val height :Float = w * s + h * c

    override fun onDrawShadow(canvas: Canvas) {
        canvas.rotate(view.rotation, width / 2, height / 2)
        canvas.translate(
            (width - view.width) / 2,
            (height - view.height) / 2
        )
        super.onDrawShadow(canvas)
    }

    override fun onProvideShadowMetrics(
        shadowSize: Point,
        shadowTouchPoint: Point
    ) {
        shadowSize[width.toInt()] = height.toInt()
        shadowTouchPoint[shadowSize.x / 2] = shadowSize.y / 2
    }
}