package com.ditto.login.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

class SvgBitmapDecoder(private val bitmapPool: BitmapPool) :
    ResourceDecoder<InputStream?, Bitmap> {

    constructor(context: Context?) : this(Glide.get(context).bitmapPool) {}

    @Throws(IOException::class)
    override fun decode(
        source: InputStream?,
        width: Int,
        height: Int
    ): Resource<Bitmap> {
        var width = width
        var height = height
        return try {
            val svg = SVG.getFromInputStream(source)
            if (width == Target.SIZE_ORIGINAL && height == Target.SIZE_ORIGINAL) {
                width = svg.documentWidth.toInt()
                height = svg.documentHeight.toInt()
                if (width <= 0 || height <= 0) {
                    val viewBox = svg.documentViewBox
                    width = viewBox.width().toInt()
                    height = viewBox.height().toInt()
                }
            } else {
                if (width == Target.SIZE_ORIGINAL) {
                    width = (height * svg.documentAspectRatio).toInt()
                }
                if (height == Target.SIZE_ORIGINAL) {
                    height = (width / svg.documentAspectRatio).toInt()
                }
            }
            require(!(width <= 0 || height <= 0)) { "Either the Target or the SVG document must declare a size." }
            val bitmap = findBitmap(width, height)
            val canvas = Canvas(bitmap!!)
            svg.renderToCanvas(canvas)
            BitmapResource.obtain(bitmap, bitmapPool)
        } catch (ex: SVGParseException) {
            throw IOException("Cannot load SVG from stream", ex)
        }
    }

    private fun findBitmap(width: Int, height: Int): Bitmap? {
        var bitmap = bitmapPool[width, height, Bitmap.Config.ARGB_8888]
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        return bitmap
    }

    override fun getId(): String {
        return javaClass.simpleName
    }

}