package com.ditto.workspace.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import com.ditto.workspace.domain.model.WorkspaceItems
import com.ditto.workspace.ui.PinchAndZoom
import trace.workspace.ui.R
import com.ditto.workspace.ui.util.Draggable.DRAG_MOVE_TOLERANCE
import com.ditto.workspace.ui.util.Draggable.DRAG_TOLERANCE
import com.ditto.workspace.ui.util.Draggable.DURATION_MILLIS
import com.ditto.workspace.ui.util.Draggable.SELECT_ALL_TOLERANCE
import java.lang.ref.WeakReference
import kotlin.math.*

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun View.makeDraggable(
    stickyAxis: Draggable.STICKY = Draggable.STICKY.NONE,
    workspaceItem: WorkspaceItems?,
    addedViews: MutableList<View>,
    isSelectAll: ObservableBoolean,
    draggableListener: DraggableListener? = null
) {
    var widgetDX = 0f
    var widgetDY = 0f

    var checkSelectAllX = 0F
    var checkSelectAllY = 0F

    var splicedXdirection = 0f
    var splicedYdirection = 0f
    var originalXdirection = 0f
    var originalYdirection = 0f
    var originalRotation = 0f
    var mDetector = GestureDetector(context,
        MyGestureListener(
            context,
            workspaceItem?.imagePath
        )
    )
    var mMultiTouchGestureDetector =
        MultiTouchGestureDetector(
            context,
            MultiTouchGestureDetectorListener()
        )

    if (workspaceItem?.spliceDirection.equals("Splice Left-to-Right") ||
        workspaceItem?.spliceDirection.equals("Splice Top-to-Bottom")
    ) {
        splicedXdirection = workspaceItem?.xcoordinate ?: 0F
        splicedYdirection = workspaceItem?.ycoordinate ?: 0F
    }
    setOnTouchListener { v, event ->
        Log.d("TAG", "TouchListener  : " + isSelectAll.get() + "   " + event.actionMasked)
        val viewParent = v.parent as View
        val viewWidthMiddle = (v.width.toFloat() / 2)
        val viewHeightMiddle = (v.height.toFloat() / 2)
        val parentHeight = (viewParent.height).toFloat()
        val parentWidth = (viewParent.width).toFloat()
        val xMax = (parentWidth - v.width.toFloat())
        val yMax = (parentHeight - v.height.toFloat())

        var x_max_Point =
            (viewWidthMiddle * Math.abs(cos(Math.toRadians(v.rotation.toDouble()))) + viewHeightMiddle * Math.abs(
                sin(
                    Math.toRadians(v.rotation.toDouble())
                )
            )).toFloat()
        var y_max_Point =
            (viewHeightMiddle * Math.abs(cos(Math.toRadians(v.rotation.toDouble()))) + viewWidthMiddle * Math.abs(
                sin(
                    Math.toRadians(v.rotation.toDouble())
                )
            )).toFloat()
        var xDiff = (viewWidthMiddle - x_max_Point)
        var yDiff = (viewHeightMiddle - y_max_Point)

        var newTouchX = event.rawX + widgetDX
        var newTouchY = event.rawY + widgetDY
        var workspaceBitmap: WeakReference<Bitmap> =
            CollisionUtil.getBitmap(v, addedViews)


        mDetector.onTouchEvent(event)
        if (workspaceItem?.splicedImages?.size == 0 && !isSelectAll.get()) {
            mMultiTouchGestureDetector.onTouchEvent(event, v)
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!isSelectAll.get()) {
                    widgetDX = v.x - event.rawX
                    widgetDY = v.y - event.rawY
                    workspaceItem?.xcoordinate = v.x
                    workspaceItem?.ycoordinate = v.y
                    workspaceItem?.pivotX = v.pivotX
                    workspaceItem?.pivotY = v.pivotY
                    workspaceItem?.rotationAngle = v.rotation
                    originalXdirection = v.x
                    originalYdirection = v.y
                    originalRotation = v.rotation
                    draggableListener?.onTouch(v, workspaceItem)
                } else {
                    checkSelectAllX = event.rawX
                    checkSelectAllY = event.rawY
                }
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("TAG", "ACTION_MOVE: ")
                if (!isSelectAll.get()) {
                    val image: ImageView? = v?.findViewById<ImageView>(R.id.imgPhotoEditorImage)
                    if (mMultiTouchGestureDetector.fingereCount < 2 && image?.colorFilter != null) {
                        // for removing drag while rotation and dragging only for selected piece
                        var isPositionChanged: Boolean = false
                        var oldX = v.x
                        var oldY = v.y
                        var newX = newTouchX
                        var newY = newTouchY
                        newX = max(0F - xDiff, newX)
                        newX = min(xMax.toFloat() + xDiff, newX)
                        newY = max(0F - yDiff, newY)
                        newY = min(yMax.toFloat() + yDiff, newY)

                        // Checking boundaries
                        if (abs(oldX - newX) <= DRAG_MOVE_TOLERANCE &&
                            abs(oldY - newY) <= DRAG_MOVE_TOLERANCE
                        ) {
                            v.x = newX
                            v.y = newY
                            isPositionChanged = true
                        }

                        // checking outside drag to cut bin
                        if (newTouchX < 0F - xDiff - x_max_Point ||
                            newTouchX > xMax.toFloat() + xDiff + x_max_Point ||
                            newTouchY < 0F - yDiff - y_max_Point ||
                            newTouchY > yMax.toFloat() + yDiff + y_max_Point
                        ) {
                            if (!((v.x < 0F - xDiff || v.x > xMax.toFloat() + xDiff ||
                                        v.y < 0F - yDiff || v.y > yMax.toFloat() + yDiff)
                                        && v.rotation != 0F)
                            ) {
                                //check overlap with other images in workspace

                                // TODO : PUT isOverlappingEnabled true IF OVERLAPPING NEEDED

                                if (Utility.isOverlappingEnabled.get()) {
                                    handleOverlapImage(
                                        v,
                                        draggableListener,
                                        originalXdirection,
                                        originalYdirection,
                                        originalRotation,
                                        xDiff,
                                        yDiff,
                                        workspaceBitmap,
                                        addedViews,
                                        workspaceItem,
                                        false
                                    )
                                }

                                // check and bring back image if exceeds workspace while rotation
                                handleRotationOutsideWorkspace(
                                    v,
                                    draggableListener,
                                    originalXdirection,
                                    originalYdirection,
                                    originalRotation,
                                    xMax,
                                    yMax,
                                    xDiff,
                                    yDiff,
                                    workspaceItem
                                )

                                // Make the Spliced image drag to respective corners after changing to new position
                                handleSplicedImageDragBack(
                                    stickyAxis,
                                    v,
                                    draggableListener,
                                    splicedXdirection,
                                    splicedYdirection,
                                    xMax,
                                    yMax,
                                    workspaceItem,
                                    true
                                )

                                if (mMultiTouchGestureDetector.fingereCount < 2 && isPositionChanged) {
                                    draggableListener?.onDragOut(v, workspaceItem)
                                }
                                Log.d(
                                    "TAG",
                                    "DragOut  : " + mMultiTouchGestureDetector.fingereCount
                                )
                            }
                        }
                        Utility.onDrag.set(true)
                        draggableListener?.onPositionChanged(v, workspaceItem)
                    }
                } else {
                    if (abs(checkSelectAllX - event.rawX) > SELECT_ALL_TOLERANCE &&
                        abs(checkSelectAllY - event.rawY) > SELECT_ALL_TOLERANCE
                    ) {
                        Log.d("TAG", "onSelectAll: ")
                        draggableListener?.onSelectAll()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.d("TAG", "ACTION_UP  : ")
                if (isSelectAll.get()) {
                    Log.d("TAG", "Select all Click  : ")
                    //get click to individual item if select all is clicked
                    draggableListener?.onTouch(v, workspaceItem)
                } else {
                    //check overlap with other images in workspace
                    // TODO : PUT isOverlappingEnabled true IF OVERLAPPING NEEDED
                    if (Utility.isOverlappingEnabled.get()) {
                        handleOverlapImage(
                            v,
                            draggableListener,
                            originalXdirection,
                            originalYdirection,
                            originalRotation,
                            xDiff,
                            yDiff,
                            workspaceBitmap,
                            addedViews,
                            workspaceItem,
                            true
                        )
                    }

                    // check and bring back image if exceeds workspace while rotation
                    handleRotationOutsideWorkspace(
                        v,
                        draggableListener,
                        originalXdirection,
                        originalYdirection,
                        originalRotation,
                        xMax,
                        yMax,
                        xDiff,
                        yDiff,
                        workspaceItem
                    )
                    Log.d(
                        "Matrix",
                        "ACTION UP : " + v.x + "   " + v.y + " ||  " + workspaceItem?.xcoordinate + "   " + workspaceItem?.ycoordinate
                    )

                    // Make the Spliced image drag to respective corners after changing to new position
                    handleSplicedImageDragBack(
                        stickyAxis,
                        v,
                        draggableListener,
                        splicedXdirection,
                        splicedYdirection,
                        xMax,
                        yMax,
                        workspaceItem,
                        false
                    )

                    if (abs(v.x - originalXdirection) <= DRAG_TOLERANCE &&
                        abs(v.y - originalYdirection) <= DRAG_TOLERANCE
                    ) {
                        performClick()
                    }
                    if (v != null && Utility.onDrag.get() &&
                        stickyAxis == Draggable.STICKY.NONE
                    ) {
                        Log.d(
                            "Matrix",
                            "ACTION UP END : " + v.x + "   " + v.y + " ||  " + workspaceItem?.xcoordinate + "   " + workspaceItem?.ycoordinate
                        )
                        draggableListener?.onPositionChanged(v, workspaceItem)
                        draggableListener?.onDragCompleted()
                    }
                }
            }
            else -> return@setOnTouchListener false
        }
        if (workspaceBitmap != null) {
            workspaceBitmap.get()?.recycle()
            workspaceBitmap
        }
        true
    }
}

/*
Check and bring back image if exceeds workspace while rotation
 */
fun handleOverlapImage(
    v: View,
    draggableListener: DraggableListener?,
    originalXdirection: Float,
    originalYdirection: Float,
    originalRotation: Float,
    xDiff: Float,
    yDiff: Float,
    bigBitmap: WeakReference<Bitmap>,
    addedViews: MutableList<View>,
    workspaceItem: WorkspaceItems?,
    showToast: Boolean
) {
    if (addedViews.size > 1) {
        var touchedView =
            CollisionUtil.getViewBitmap(v)
        if (CollisionUtil.isCollisionDetected(
                touchedView,
                v.x.toInt(),
                v.y.toInt(),
                xDiff.toInt(),
                yDiff.toInt(),
                bigBitmap
            )
        ) {
            if (bigBitmap.get() != null && touchedView.get() != null)
                draggableListener?.onOverlapped(showToast)
            Utility.onDrag.set(false)
            v.x = originalXdirection
            v.y = originalYdirection
            v.rotation = originalRotation
            draggableListener?.onPositionChanged(v, workspaceItem)
        }
        if (touchedView != null) {
            touchedView.get()?.recycle()
            touchedView
        }
    }
}

/*
Check and bring back image if exceeds workspace while rotation
 */
fun handleRotationOutsideWorkspace(
    v: View,
    draggableListener: DraggableListener?,
    originalXdirection: Float,
    originalYdirection: Float,
    originalRotation: Float,
    xMax: Float,
    yMax: Float,
    xDiff: Float,
    yDiff: Float,
    workspaceItem: WorkspaceItems?
) {
    if ((v.x < 0F - xDiff ||
                v.x > xMax.toFloat() + xDiff ||
                v.y < 0F - yDiff ||
                v.y > yMax.toFloat() + yDiff)
//        && v.rotation != 0F
    ) {
        Utility.onDrag.set(false)
        v.rotation = originalRotation
        v.x = originalXdirection
        v.y = originalYdirection
        draggableListener?.onPositionChanged(v, workspaceItem)
    }
}

/*
Make the Spliced image drag to respective corners after changing to new position
 */
fun handleSplicedImageDragBack(
    stickyAxis: Draggable.STICKY,
    v: View,
    draggableListener: DraggableListener?,
    splicedXdirection: Float,
    splicedYdirection: Float,
    xMax: Float,
    yMax: Float,
    workspaceItem: WorkspaceItems?,
    isDragOut: Boolean
) {
    when (stickyAxis) {
        Draggable.STICKY.AXIS_X_START -> {
            v.animate().x(0F).y(splicedYdirection).setDuration(DURATION_MILLIS)
                .setUpdateListener {
                    draggableListener?.onPositionChanged(
                        v,
                        workspaceItem
                    )
                }
                .start()
        }
        Draggable.STICKY.AXIS_X_END -> {
            v.animate().x(xMax)
                .setDuration(DURATION_MILLIS)
                .setUpdateListener {
                    draggableListener?.onPositionChanged(
                        v,
                        workspaceItem
                    )
                }.withEndAction {
                    Log.d(
                        "Matrix",
                        "SPLICE UP: " + v.x + "   " + v.y + " ||  " + workspaceItem?.xcoordinate + "   " + workspaceItem?.ycoordinate
                    )
                    // project image only when dragged inside workspace
                    if (!isDragOut) {
                        draggableListener?.onDragCompleted()
                    }
                }
                .start()
        }
        Draggable.STICKY.AXIS_Y_START -> {
            v.animate().y(0F)
                .setDuration(DURATION_MILLIS)
                .setUpdateListener {
                    draggableListener?.onPositionChanged(
                        v,
                        workspaceItem
                    )
                }.withEndAction {
                    Log.d(
                        "Matrix",
                        "SPLICE UP: " + v.x + "   " + v.y + " ||  " + workspaceItem?.xcoordinate + "   " + workspaceItem?.ycoordinate
                    )
                    // project image only when dragged inside workspace
                    if (!isDragOut) {
                        draggableListener?.onDragCompleted()
                    }
                }.start()
        }
        Draggable.STICKY.AXIS_Y_END -> {
            v.animate().x(splicedXdirection).y(yMax)
                .setDuration(DURATION_MILLIS)
                .setUpdateListener {
                    draggableListener?.onPositionChanged(
                        v,
                        workspaceItem
                    )
                }.start()
        }
        Draggable.STICKY.NONE -> {

        }
    }
}

/*
Show pinch and zoom pop up
 */
fun showPinchZoomPopup(context: Context, imagePath: String?) {
    Utility.isPopupShowing.set(true)
    val intent = Intent(context, PinchAndZoom::class.java)
    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    intent.putExtra("ImageURL", imagePath)
    ContextCompat.startActivity(context, intent, null)
}


private class MultiTouchGestureDetectorListener(draggableListener: DraggableListener? = null) :
    MultiTouchGestureDetector.SimpleOnMultiTouchGestureListener() {

    var mRotation = 0.0f
    override fun onScale(detector: MultiTouchGestureDetector?, image: View?) {
        Log.d("TAG", "SCALE: \$rotation $mRotation")
    }

    override fun onMove(detector: MultiTouchGestureDetector?, image: View?) {
        Log.d("TAG", "MOVE: ")
    }

    override fun onRotate(detector: MultiTouchGestureDetector?, view: View?) {
        mRotation += detector?.getRotation()!!
        val imageView: ImageView? = view?.findViewById<ImageView>(R.id.imgPhotoEditorImage)
        if (imageView?.colorFilter != null) {
            move(view, false, mRotation)
            Log.d("TAG", "ROTATE: " + mRotation)
            Log.d("TAG", "PivotX : " + view.pivotX)
            Log.d("TAG", "PivotY : " + view.pivotX)
            Log.d("TAG", "Width : " + view.width)
            Log.d("TAG", "Height : " + view.height)
        }
    }

    override fun onBegin(detector: MultiTouchGestureDetector?): Boolean {
        Log.d("TAG", "BEGIN: ")
        return super.onBegin(detector)
    }

    override fun onEnd(detector: MultiTouchGestureDetector?, view: View?) {
        super.onEnd(detector, view)
        val imageView: ImageView? = view?.findViewById<ImageView>(R.id.imgPhotoEditorImage)
        if (imageView?.colorFilter != null) {
            Log.d("TAG", "END: ")
            move(view, true, 0f)
        }
        mRotation = 0.0f
    }

    private fun move(view: View?, isScaleEnd: Boolean, rotateAngle: Float) {
        if (isScaleEnd) {
            view?.pivotX = (view?.width?.toFloat()?.div(2))!!
            view.pivotY = (view.height?.toFloat().div(2))
            view?.rotation = view?.rotation?.let { round(it, 45).toFloat() }!!
        } else {
            view?.pivotX = (view?.width?.toFloat()?.div(2))!!
            view.pivotY = (view.height?.toFloat().div(2))
            val rotation =
                rotateAngle.let { view.rotation.plus(it).let { adjustAngle(it) } }
            view.rotation = rotation
        }
    }

    /*
    Round to nearest angle while rotating
    */
    fun round(num: Float, multipleOf: Int): Double {
        return Math.floor((num + multipleOf / 2) / multipleOf.toDouble()) * multipleOf
    }

    /*
    Adjusting angles to get the correcct rotation angle
    */
    private fun adjustAngle(degrees: Float): Float {
        var degrees = degrees
        if (degrees > 180.0F) {
            degrees -= 360.0F
        } else if (degrees < -180.0F) {
            degrees += 360.0F
        }
        return degrees
    }
}

private class MyGestureListener(context: Context, path: String?) :
    GestureDetector.SimpleOnGestureListener() {

    var popUpContext = context
    var imagePath = path

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        Log.d("TAG", "DoubleTap: ")
        Utility.onDrag.set(false)
        if (!Utility.isPopupShowing.get()) {
            showPinchZoomPopup(
                popUpContext,
                imagePath
            )
        }
        return super.onDoubleTap(e)
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Log.d("TAG", "onSingleTapConfirmed: ")
        Utility.onDrag.set(false)
        return super.onSingleTapConfirmed(e)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.d("TAG", "onSingleTapUp: ")
        Utility.onDrag.set(false)
        return super.onSingleTapUp(e)
    }

    override fun onLongPress(e: MotionEvent?) {
        Log.d("TAG", "OnLongPress: ")
        super.onLongPress(e)
    }

}