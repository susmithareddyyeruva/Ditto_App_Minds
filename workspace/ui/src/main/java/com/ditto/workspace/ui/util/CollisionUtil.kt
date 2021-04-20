package com.ditto.workspace.ui.util

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import com.ditto.workspace.ui.R
import java.lang.ref.WeakReference


object CollisionUtil {

    /**
     * Check pixel-perfectly if two views are colliding
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun isCollisionDetected(
        viewBitmap: WeakReference<Bitmap>,
        x1: Int, y1: Int,
        xDiff: Int, yDiff: Int,
        bigBitmap: WeakReference<Bitmap>
    ): Boolean {
        require(!(viewBitmap == null || bigBitmap == null)) { "bitmaps cannot be null" }
        val bounds1 = Rect(
            x1 + xDiff,
            y1 + yDiff,
            ((x1 + xDiff) + (viewBitmap.get()?.width ?: 0)),
            ((y1 + yDiff) + (viewBitmap.get()?.height ?: 0))
        )

        val bounds2 = Rect(
            0,
            0,
            0 + (bigBitmap.get()?.width ?: 0),
            0 + (bigBitmap.get()?.height ?: 0)
        )
        if (Rect.intersects(bounds1, bounds2)) {
            val collisionBounds =
                getCollisionBounds(
                    bounds1,
                    bounds2
                )
            for (i in collisionBounds.left until collisionBounds.right) {
                for (j in collisionBounds.top until collisionBounds.bottom) {
                    val bitmap1Pixel = viewBitmap?.get()?.getPixel(
                        (i - (x1 + xDiff)),
                        (j - (y1 + yDiff))
                    )
                    val bitmap2Pixel = bigBitmap.get()?.getPixel(i - 0, j - 0)
                    if (isFilled(
                            bitmap1Pixel
                        ) && isFilled(
                            bitmap2Pixel
                        )
                    ) {
                        viewBitmap.get()?.recycle()
                        bigBitmap.get()?.recycle()
                        System.gc()
                        return true
                    }
                }
            }
        }

        viewBitmap.get()?.recycle()
        bigBitmap.get()?.recycle()
        System.gc()
        return false
    }


    /**
     * Check pixel-perfectly if two views are colliding
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun isBoundaryDetected(
        viewBitmap: WeakReference<Bitmap>,
        x1: Int, y1: Int,
        xDiff: Int, yDiff: Int,
        parentView: View
    ): Boolean {
        val bounds1 = Rect(
            x1 + xDiff,
            y1 + yDiff,
            ((x1 + xDiff) + (viewBitmap.get()?.width ?: 0)),
            ((y1 + yDiff) + (viewBitmap.get()?.height ?: 0))
        )

        val bounds2 = Rect(
            0,
            0,
            parentView.width,
            parentView.height
        )
        if (Rect.intersects(bounds1, bounds2)) {
            val collisionBounds =
                getCollisionBounds(
                    bounds1,
                    bounds2
                )
            for (i in collisionBounds.left until collisionBounds.right) {
                for (j in collisionBounds.top until collisionBounds.bottom) {
                    if (i == collisionBounds.left || j == collisionBounds.top || i == collisionBounds.right || j == collisionBounds.bottom) {
                        val bitmap1Pixel = viewBitmap?.get()?.getPixel(
                            (i - (x1 + xDiff)),
                            (j - (y1 + yDiff))
                        )
                        if (isFilled(
                                bitmap1Pixel
                            )
                        ) {
                            viewBitmap.get()?.recycle()
                            System.gc()
                            return true
                        }
                    }

                }
            }
        }

        viewBitmap.get()?.recycle()
        System.gc()
        return false
    }


    fun getBitmap(
        view1: View,
        parentView: MutableList<View>
    ): WeakReference<Bitmap> {
        var bigBitmap = WeakReference(
            Bitmap.createBitmap(
                ((view1.parent as View).parent as View)?.measuredWidth,
                ((view1.parent as View).parent as View)?.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
        )
        val canvas = bigBitmap.get()?.let { Canvas(it) }
        for (image in parentView) {
            if (image.id != view1.id) {
                val myIcon = (image.findViewById(R.id.imgPhotoEditorImage) as ImageView).drawable
                //**********************
                val matrix = Matrix()
                matrix.preTranslate(
                    image.x,
                    image.y
                )
                matrix.preRotate(
                    image.rotation,
                    image.pivotX,
                    image.pivotY
                )
                matrix.preScale(
                    // interchanged rotation x, rotation y as its on landscape
                    if ((image.findViewById(R.id.imgPhotoEditorImage) as ImageView).rotationY == 0F) 1F else -1F,
                    if ((image.findViewById(R.id.imgPhotoEditorImage) as ImageView).rotationX == 0F) 1F else -1F,
                    (image.findViewById(R.id.imgPhotoEditorImage) as ImageView).width.toFloat() / 2,
                    (image.findViewById(R.id.imgPhotoEditorImage) as ImageView).height.toFloat() / 2
                );
                //**********************
                var bitmap: Bitmap? = myIcon.toBitmap(image.width, image.height)
                bitmap?.let {
                    canvas?.drawBitmap(
                        it,
                        matrix,
                        null
                    )
                }
                matrix.reset()
                //******************************
            }
        }
        return bigBitmap
    }

    fun drawableToBitmap(drawable: Drawable?, scaleFactor: Double): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable: BitmapDrawable = drawable as BitmapDrawable
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap()
            }
        }
        bitmap =
            if (drawable?.getIntrinsicWidth() ?: 0 <= 0 || drawable?.getIntrinsicHeight() ?: 0 <= 0) {
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
            } else {
                drawable?.let {
                    Bitmap.createBitmap(
                        Math.ceil(it.getIntrinsicWidth().div(scaleFactor)).toInt(),
                        Math.ceil(it.getIntrinsicHeight().div(scaleFactor)).toInt(),
                        Bitmap.Config.ARGB_8888
                    )
                }
            }
        val canvas = bitmap?.let { Canvas(it) }
        canvas?.let { drawable?.setBounds(0, 0, it.width, it.height) }
        canvas?.let { drawable?.draw(it) }
        return bitmap
    }

    /**
     * Get the collision bounds from two rects
     *
     * @param rect1 Rect
     * @param rect2 Rect
     * @return Rect
     */
    private fun getCollisionBounds(
        rect1: Rect,
        rect2: Rect
    ): Rect {
        val left = Math.max(rect1.left, rect2.left)
        val top = Math.max(rect1.top, rect2.top)
        val right = Math.min(rect1.right, rect2.right)
        val bottom = Math.min(rect1.bottom, rect2.bottom)
        return Rect(left, top, right, bottom)
    }

    /**
     * Check if pixel is not transparent
     *
     * @param pixel int
     * @return boolean
     */
    private fun isFilled(pixel: Int?): Boolean {
        return pixel != Color.TRANSPARENT
    }

    /**
     * Get a Bitmap from a specified View
     *
     * @param v View
     * @return Bitmap
     */
    fun getViewBitmap(v: View?): WeakReference<Bitmap> {
        val matrix = Matrix()
        matrix.preRotate(v?.rotation ?: 0F)
        if (v?.measuredHeight ?: 0 <= 0) {
            val specWidth = View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
            v?.measure(specWidth, specWidth)
            val b =
                WeakReference(
                    Bitmap.createBitmap(
                        v?.width ?: 0, v?.height ?: 0,
                        Bitmap.Config.ARGB_8888
                    )
                )
            val c = Canvas(b.get()!!)
            v?.layout(0, 0, v.measuredWidth, v.measuredHeight)
            v?.draw(c)
//            return b
            return WeakReference(
                Bitmap.createBitmap(
                    b.get()!!,
                    0,
                    0,
                    b.get()!!.width,
                    b.get()!!.height,
                    matrix,
                    true
                )
            )
        }
        val b =
            WeakReference(
                Bitmap.createBitmap(
                    v?.width ?: 0,
                    v?.height ?: 0,
                    Bitmap.Config.ARGB_8888
                )
            )
        val c = Canvas(b.get()!!)
        v?.layout(v.left, v.top, v.right, v.bottom)
        v?.draw(c)
//        return b
        return WeakReference(
            Bitmap.createBitmap(
                b.get()!!,
                0,
                0,
                b.get()!!.width,
                b.get()!!.height,
                matrix,
                true
            )
        )
    }
}