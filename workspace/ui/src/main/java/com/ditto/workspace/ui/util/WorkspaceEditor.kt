package com.ditto.workspace.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import com.ditto.workspace.domain.model.DragData
import com.ditto.workspace.domain.model.WorkspaceItems
import com.ditto.workspace.ui.R
import com.ditto.workspace.ui.util.ViewType.IMAGE
import core.ui.common.Utility.Companion.adjustAngle
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.*

class WorkspaceEditor private constructor(builder: Builder) {
    private val mLayoutInflater: LayoutInflater
    private val context: Context
    private val parentView: WorkspaceEditorView?
    private var imageView: ImageView?
    private val addedViews: MutableList<View>
    private val addedViewsModel: MutableList<WorkspaceItems>
    private val redoViews: MutableList<View>
    private val redoViewsModel: MutableList<WorkspaceItems>
    private var mOnWorkspaceImageDragListener: DraggableListener? = null
    private var dragListener: DraggableListener? = null
    private var isSelectAll: ObservableBoolean = ObservableBoolean(true)

    /**
     * Add image to workspace layout
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun addImage(
        imageBitmap: Bitmap?,
        workspaceItem: WorkspaceItems?,
        scaleFactor: Double,
        showProjection: Boolean,
        isDraggedPiece: Boolean,
        patternName: String?,
        dragListener: DraggableListener,
        patternDownloadFolderName: String
    ) {
        mOnWorkspaceImageDragListener = dragListener
        val imageRootView = getLayout(IMAGE)
        val imageV =
            imageRootView?.findViewById<ImageView>(R.id.imgPhotoEditorImage)
        val tempimageBitmap = imageBitmap
//        imageV?.maxWidth =
//            ceil(tempDrawable?.intrinsicWidth?.toFloat()?.div(scaleFactor) ?: 1.0).toInt()
//        imageV?.maxHeight =
//            ceil(tempDrawable?.intrinsicHeight?.toFloat()?.div(scaleFactor) ?: 1.0).toInt()
        imageV?.maxWidth =
            ceil(tempimageBitmap?.width?.toFloat()?.div(scaleFactor) ?: 1.0).toInt()
        imageV?.maxHeight =
            ceil(tempimageBitmap?.height?.toFloat()?.div(scaleFactor) ?: 1.0).toInt()
        Log.d("TAG", "maxWidth : " + imageV?.maxWidth)
        Log.d("TAG", "maxHeight : " + imageV?.maxHeight)
        imageV?.scaleType = ImageView.ScaleType.FIT_XY
        imageV?.setImageBitmap(tempimageBitmap)
        var sticky: Draggable.STICKY =
            Draggable.STICKY.NONE
        val params =
            RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        if (workspaceItem != null) {
            initialPosition(isDraggedPiece, workspaceItem, imageV)
           // if (workspaceItem.spliceDirection.equals("Splice Left-to-Right")) {
            if (workspaceItem.splicedImages?.size ?: 0 > 1) {
                workspaceItem.xcoordinate = 0F
                params.addRule(if (workspaceItem.currentSplicedPieceColumn != 0) RelativeLayout.ALIGN_PARENT_LEFT else RelativeLayout.ALIGN_PARENT_RIGHT);
                sticky =
                    if (workspaceItem.currentSplicedPieceColumn!=0) Draggable.STICKY.AXIS_X_START else Draggable.STICKY.AXIS_X_END
            //} else if (workspaceItem.spliceDirection.equals("Splice Top-to-Bottom")) {

            } /*else if (workspaceItem.spliceDirection ?: 0 > 1) {
                workspaceItem.ycoordinate = 0F
                params.addRule(if (workspaceItem.currentSplicedPieceRow != 0) RelativeLayout.ALIGN_PARENT_BOTTOM else RelativeLayout.ALIGN_PARENT_TOP);
                sticky =
                    if (workspaceItem.currentSplicedPieceRow!=0) Draggable.STICKY.AXIS_Y_END else Draggable.STICKY.AXIS_Y_START
            //} else if (workspaceItem.spliceDirection.equals("Splice Multiple-to-Multiple")) {
            } else if (workspaceItem.spliceDirection ?: 0 > 1) {

                workspaceItem.xcoordinate = 0F
                params.addRule(if (workspaceItem.currentSplicedPieceColumn != 0) RelativeLayout.ALIGN_PARENT_LEFT else RelativeLayout.ALIGN_PARENT_RIGHT);
                sticky =
                    if (workspaceItem.currentSplicedPieceColumn != 0) Draggable.STICKY.AXIS_X_START else Draggable.STICKY.AXIS_X_END
            }*/
            imageRootView?.tag = workspaceItem.id
        }
        // checking overlapping only for dragged pieces
        // TODO : PUT isOverlappingEnabled true IF OVERLAPPING NEEDED
        if (Utility.isOverlappingEnabled.get() && isDraggedPiece && checkOverlappingCondition(
//                CollisionUtil.drawableToBitmap(
//                    imageBitmap,
//                    scaleFactor
//                ),
                imageBitmap,
                null,
                workspaceItem?.xcoordinate?.toInt(),
                workspaceItem?.ycoordinate?.toInt()
            )
        ) {
            dragListener.onOverlapped(true)
            return
        }

        imageRootView?.makeDraggable(
            sticky,
            workspaceItem,
            addedViews,
            isSelectAll,
            patternName,
            patternDownloadFolderName,
            object : DraggableListener {
                override fun onPositionChanged(view: View, workspaceItem: WorkspaceItems?) {
                    workspaceItem?.xcoordinate = view.x
                    workspaceItem?.ycoordinate = view.y
                    workspaceItem?.rotationAngle = view.rotation
                    addedViewsModel.find { it.id == workspaceItem?.id } to (workspaceItem)
                    clearAllSelection()
                    imageView = view.findViewById(R.id.imgPhotoEditorImage) as ImageView
                    imageView?.tag = workspaceItem?.id
                    workspaceItem?.pivotX = imageView?.pivotX ?: view.pivotX
                    workspaceItem?.pivotY = imageView?.pivotY ?: view.pivotY
                    imageView?.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.app_red
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    dragListener.onPositionChanged(view, workspaceItem)
                }

                override fun onTouch(view: View, workspaceItem: WorkspaceItems?) {
                    clearAllSelection()
                    dragListener.onTouch(view, workspaceItem)
                    imageView = view.findViewById(R.id.imgPhotoEditorImage) as ImageView
                    imageView?.tag = workspaceItem?.id
                    imageView?.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.app_red
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                }

                override fun onSelectAll() {
                    if (isSelectAll.get()) {
                        selectAllCopyWorkSpaceLayout()
                    }
                }

                override fun onDragCompleted() {
                    dragListener.onDragCompleted()
                }

                override fun onOverlapped(showToast: Boolean) {
                    dragListener.onOverlapped(showToast)
                }

                override fun onRotationOutOfWorkspace() {
                    dragListener.onRotationOutOfWorkspace()
                }

                override fun onProjectWorkspace() {
                    //TODO("Not yet implemented")
                }

                override fun onDragOut(view: View, workspaceItem: WorkspaceItems?) {
                    val state = workspaceItem?.id?.let {
                        DragData(
                            Draggable.DRAG_OUT_WORKSPACE,
                            it, view.width, view.height, null, workspaceItem
                        )
                    }
                    val shadow =
                        DragShadowBuilder(view)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        // Fix for Android 9. On Drag receiving local state as null
                        Utility.dragData.set(state)
                        view.startDragAndDrop(null, shadow, state, 0)
                    } else {
                        Utility.dragData.set(state)
                        view.startDrag(null, shadow, state, 0)
                    }
                }
            })
        parentView?.addView(imageRootView, params)
        addViewToParent(imageRootView, imageV, workspaceItem)
        if (showProjection) {
            dragListener.onDragCompleted()
        }
    }

    /**
    }
     * Check overlapping condition in workspace
     */
    private fun checkOverlappingCondition(
        image: Bitmap?,
        view: View?,
        xCoordinate: Int?,
        yCoordinate: Int?
    ): Boolean {
        var workspace: WeakReference<Bitmap>? = null
        if (view == null)
            workspace = parentView?.let {
                CollisionUtil.getBitmap(
                    it,
                    addedViews
                )
            }
        else
            workspace = view.let {
                CollisionUtil.getBitmap(
                    it,
                    addedViews
                )
            }

        if (addedViews.size > 0 && image?.let {
                workspace?.let { it1 ->
                    CollisionUtil.isCollisionDetected(
                        WeakReference(it),
                        xCoordinate ?: 0,
                        yCoordinate ?: 0,
                        0,
                        0,
                        it1
                    )
                }
            } == true
        ) {
            workspace?.get()?.recycle()
            workspace = null
            return true
        }
        workspace?.get()?.recycle()
        workspace = null
        return false
    }

    /**
     * Select all pieces to drag and making a copy of whole layout
     */
    fun selectAllDrag(view: View) {
        view.setOnTouchListener { view, motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (isSelectAll.get()) {
                        selectAllCopyWorkSpaceLayout()
                    }
                }
            }
            true
        }
    }

    /**
     * Select all making a copy of whole layout
     */
    fun selectAllCopyWorkSpaceLayout() {
        val state =
            parentView?.id?.let {
                DragData(
                    Draggable.SELECT_ALL,
                    it,
                    parentView?.width ?:0,
                    parentView?.height ?:0,
                    null,
                    null
                )
            }
        val shadow = View.DragShadowBuilder(parentView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Utility.dragData.set(state)
            parentView?.startDragAndDrop(null, shadow, state, 0)
        } else {
            Utility.dragData.set(state)
            parentView?.startDrag(null, shadow, state, 0)
        }
    }

    /**
     * Set initial position of images to be placed at workspace, "center if dragged" and "left top tip otherwise"
     */
    private fun initialPosition(
        isDraggedPiece: Boolean,
        workspaceItem: WorkspaceItems?,
        imageView: ImageView?
    ): WorkspaceItems? {
        // checking isActiveCompletedProject to project to correct x an y coordinate,
        // dragging from patter pieces to show image at centre finger tip
        if (isDraggedPiece) {
            workspaceItem?.xcoordinate =
                workspaceItem?.xcoordinate?.minus((imageView?.maxWidth ?: 0).div(2).toFloat()) ?: 0F
            workspaceItem?.ycoordinate =
                workspaceItem?.ycoordinate?.minus((imageView?.maxHeight ?: 0).div(2).toFloat())
                    ?: 0F

            // Check if the image exceeds boundaries of workspace
            val xMax = parentView?.width?.minus(imageView?.maxWidth ?: 0)
            var newX = max(0F, workspaceItem?.xcoordinate ?: 0F)
            newX = min(xMax?.toFloat() ?: 0F, newX)
            workspaceItem?.xcoordinate = newX

            val yMax = parentView?.height?.minus(imageView?.maxHeight ?: 0)
            var newY = max(0F, workspaceItem?.ycoordinate ?: 0F)
            newY = min(yMax?.toFloat() ?: 0F, newY)
            workspaceItem?.ycoordinate = newY

        } else {
            workspaceItem?.xcoordinate = workspaceItem?.xcoordinate ?: 0F
            workspaceItem?.ycoordinate = workspaceItem?.ycoordinate ?: 0F
        }
        return workspaceItem
    }

    /**
     * Adding images in parent layout
     */
    private fun addViewToParent(
        rootView: View?,
        imageV: View?,
        image: WorkspaceItems?
    ) {

        if (image != null) {
            rootView?.id = image.id
            rootView?.x = image.xcoordinate
            rootView?.y = image.ycoordinate
            rootView?.pivotX = image.pivotX
            rootView?.pivotY = image.pivotY
            rootView?.rotation = image.rotationAngle
            imageV?.rotationX = if (image.isMirrorV) 180F else 0F
            imageV?.rotationY = if (image.isMirrorH) 180F else 0F
        }

        // For fixing Virtual image spliced issue
        //if (image?.spliceDirection?.equals("Splice Left-to-Right")!!) {
        if (image?.splicedImages?.size?: 0>1) {
            image?.xcoordinate = if (image?.currentSplicedPieceRow != 0) 0F else {
                parentView?.width?.toFloat()
                    ?.minus((imageV as ImageView)?.maxWidth?.toFloat() ?: 0F)
                    ?: 0F
            }
        }
        /*//if (image?.spliceDirection!!.equals("Splice Top-to-Bottom")) {
        if (image?.spliceDirection?: 0>1) {
            image?.ycoordinate = if (image?.currentSplicedPieceColumn != 0) 0F else {
                parentView?.height?.toFloat()
                    ?.minus((imageV as ImageView)?.maxHeight?.toFloat() ?: 0F) ?: 0F
            }
        }
       // if (image?.spliceDirection!!.equals("Splice Multiple-to-Multiple")) {
        if (image?.spliceDirection?: 0>1) {
            image?.xcoordinate = if (image?.currentSplicedPieceRow != 0) 0F else {
                parentView?.width?.toFloat()
                    ?.minus((imageV as ImageView)?.maxWidth?.toFloat() ?: 0F)
                    ?: 0F
            }
        }*/

        //todo testing pending
        /*if(image?.splicedImages?.size ?:0 > 1){
            image?.xcoordinate = 0F
            image?.ycoordinate = 0F
        }*/
        //
        rootView?.let { addedViews.add(it) }
        image?.let { addedViewsModel.add(it) }
    }

    private fun getLayout(viewType: ViewType): View? {
        var rootView: View? = null
        when (viewType) {
            IMAGE -> rootView =
                mLayoutInflater.inflate(R.layout.workspace_drag_item_image, null)
            else -> {}
        }
        if (rootView != null) {
            rootView.tag = viewType

        }
        return rootView
    }

    /**
     * Get all views added to workspace
     */
    val views: List<WorkspaceItems>
        get() {
            return addedViewsModel
        }

    /**
     * Removes all the edited operations performed [WorkspaceEditorView]
     * This will also clear the undo and redo stack
     */
    fun clearAllViews() {
        for (i in addedViews.indices) {
            parentView!!.removeView(addedViews[i])
        }
        addedViews.clear()
        addedViewsModel.clear()
        redoViews.clear()
        redoViewsModel.clear()
    }

    /**
     * Remove all selection
     */
    @UiThread
    fun clearAllSelection() {
        isSelectAll.set(false)
        for (i in 0 until parentView!!.childCount) {
            val childAt = parentView?.getChildAt(i)
            val image = childAt?.findViewById<ImageView>(R.id.imgPhotoEditorImage)
            image?.clearColorFilter()
            image?.invalidate()
        }
    }

    /**
     * Select all selection
     */
    @UiThread
    fun selectAllSelection() {
        isSelectAll.set(true)
        for (i in 0 until parentView!!.childCount) {
            val childAt = parentView?.getChildAt(i)
            val image = childAt?.findViewById<ImageView>(R.id.imgPhotoEditorImage)
            image?.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_red
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    fun highlightSplicePiece() {
        for (i in 0 until parentView!!.childCount) {
            val childAt = parentView?.getChildAt(i)
            val image = childAt?.findViewById<ImageView>(R.id.imgPhotoEditorImage)
            image?.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.app_red
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    /**
     * Mirror a view Horizontal
     */
    fun flipHorizontal() {
        val orginalRotation = (imageView?.parent as View).rotation
        val orginalX = (imageView?.parent as View).x
        val orginalY = (imageView?.parent as View).y
        (imageView?.parent as View).rotation = 0F
        imageView?.rotationY = if (imageView?.rotationY == 180F) 0f else 180F

        // Check within boundaries on mirroring
        val xMax = parentView?.width?.minus(imageView?.maxWidth ?: 0)
        var newX = max(0F, (imageView?.parent as View).x ?: 0F)
        newX = min(xMax?.toFloat() ?: 0F, newX)
        (imageView?.parent as View).x = newX

        val yMax = parentView?.height?.minus(imageView?.maxHeight ?: 0)
        var newY = max(0F, (imageView?.parent as View).y ?: 0F)
        newY = min(yMax?.toFloat() ?: 0F, newY)
        (imageView?.parent as View).y = newY

        // Check if it overlaps on mirroring
        // TODO : PUT isOverlappingEnabled true IF OVERLAPPING NEEDED
        if (Utility.isOverlappingEnabled.get() && checkOverlappingCondition(
                CollisionUtil.getViewBitmap((imageView?.parent as View))
                    .get(),
                (imageView?.parent as View),
                (imageView?.parent as View).x.toInt(),
                (imageView?.parent as View).y.toInt()
            )
        ) {
            (imageView?.parent as View).rotation = orginalRotation
            imageView?.rotationY = if (imageView?.rotationY == 180F) 0f else 180F
            (imageView?.parent as View).x = orginalX
            (imageView?.parent as View).y = orginalY
            mOnWorkspaceImageDragListener?.onOverlapped(true)
            return
        }
        (addedViews.find { it.tag == imageView?.tag }) to (imageView?.parent as View)

        addedViewsModel.find { it.id == imageView?.tag }?.isMirrorH =
            (imageView?.rotationY == 180F)
        addedViewsModel.find { it.id == imageView?.tag }?.xcoordinate =
            (imageView?.parent as View).x ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.ycoordinate =
            (imageView?.parent as View).y ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.rotationAngle =
            (imageView?.parent as View).rotation ?: 0F

        addedViewsModel.find { it.id == imageView?.tag }?.transformA= if(addedViewsModel.find { it.id == imageView?.tag }?.isMirrorH == true) "-1.0" else "1.0"

        imageView?.let { mOnWorkspaceImageDragListener?.onDragCompleted() }

    }

    /**
     * Mirror a view Vertical
     */
    fun flipVertical() {
        var orginalRotation = (imageView?.parent as View).rotation
        var orginalX = (imageView?.parent as View).x
        var orginalY = (imageView?.parent as View).y
        (imageView?.parent as View).rotation = 0F
        imageView?.rotationX = if (imageView?.rotationX == 180F) 0f else 180F

        // Check within boundaries on mirroring
        val xMax = parentView?.width?.minus(imageView?.maxWidth ?: 0)
        var newX = max(0F, (imageView?.parent as View).x ?: 0F)
        newX = min(xMax?.toFloat() ?: 0F, newX)
        (imageView?.parent as View).x = newX

        val yMax = parentView?.height?.minus(imageView?.maxHeight ?: 0)
        var newY = max(0F, (imageView?.parent as View).y ?: 0F)
        newY = min(yMax?.toFloat() ?: 0F, newY)
        (imageView?.parent as View).y = newY

        // Check if it overlaps on mirroring
        if (Utility.isOverlappingEnabled.get() && checkOverlappingCondition(
                CollisionUtil.getViewBitmap((imageView?.parent as View))
                    .get(),
                (imageView?.parent as View),
                (imageView?.parent as View).x.toInt(),
                (imageView?.parent as View).y.toInt()
            )
        ) {
            (imageView?.parent as View).rotation = orginalRotation
            imageView?.rotationX = if (imageView?.rotationX == 180F) 0f else 180F
            (imageView?.parent as View).x = orginalX
            (imageView?.parent as View).y = orginalY
            mOnWorkspaceImageDragListener?.onOverlapped(true)
            return
        }
        (addedViews.find { it.tag == imageView?.tag }) to (imageView?.parent as View)
        addedViewsModel.find { it.id == imageView?.tag }?.isMirrorV =
            (imageView?.rotationX == 180F)
        addedViewsModel.find { it.id == imageView?.tag }?.xcoordinate =
            (imageView?.parent as View).x ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.ycoordinate =
            (imageView?.parent as View).y ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.rotationAngle =
            (imageView?.parent as View).rotation ?: 0F

        addedViewsModel.find { it.id == imageView?.tag }?.transformD= if(addedViewsModel.find { it.id == imageView?.tag }?.isMirrorV == true) "-1.0" else "1.0"

        imageView?.let { mOnWorkspaceImageDragListener?.onDragCompleted() }
    }

    /**
     * Rotate a view Clockwise
     */
    fun rotateClockwise() {
        var originalRotation = (imageView?.parent as View).rotation
        var originalX = (imageView?.parent as View).x
        var originalY = (imageView?.parent as View).y
        (imageView?.parent as View).pivotX = (imageView?.parent as View).width.toFloat()/2
        (imageView?.parent as View).pivotY = (imageView?.parent as View).height.toFloat()/2
        (imageView?.parent as View).rotation = adjustAngle(core.ui.common.Utility.round((imageView?.parent as View).rotation + 45F,45).toFloat())

        // Check within boundaries on rotation
        checkRotateOutOfWorkspace(originalRotation,originalX,originalY)

        // Check if it overlaps on mirroring
        if (Utility.isOverlappingEnabled.get() && checkOverlappingCondition(
                CollisionUtil.getViewBitmap((imageView?.parent as View))
                    .get(),
                (imageView?.parent as View),
                (imageView?.parent as View).x.toInt(),
                (imageView?.parent as View).y.toInt()
            )
        ) {
            (imageView?.parent as View).rotation = originalRotation
            (imageView?.parent as View).x = originalX
            (imageView?.parent as View).y = originalY
            mOnWorkspaceImageDragListener?.onOverlapped(true)
            return
        }
        (addedViews.find { it.tag == imageView?.tag }) to (imageView?.parent as View)
        addedViewsModel.find { it.id == imageView?.tag }?.xcoordinate =
            (imageView?.parent as View).x ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.ycoordinate =
            (imageView?.parent as View).y ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.rotationAngle =
            (imageView?.parent as View).rotation ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.pivotX =
            (imageView?.parent as View).pivotX ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.pivotY =
            (imageView?.parent as View).pivotY ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.transformD= if(addedViewsModel.find { it.id == imageView?.tag }?.isMirrorV == true) "-1.0" else "1.0"

        imageView?.let { mOnWorkspaceImageDragListener?.onDragCompleted() }
    }

    /**
     * Rotate a view Anti- Clockwise
     */
    fun rotateAntiClockwise() {
        var originalRotation = (imageView?.parent as View).rotation
        var originalX = (imageView?.parent as View).x
        var originalY = (imageView?.parent as View).y
        (imageView?.parent as View).pivotX = (imageView?.parent as View).width.toFloat()/2
        (imageView?.parent as View).pivotY = (imageView?.parent as View).height.toFloat()/2
        (imageView?.parent as View).rotation = adjustAngle(core.ui.common.Utility.round((imageView?.parent as View).rotation - 45F,45).toFloat())

        // Check within boundaries on rotation
        checkRotateOutOfWorkspace(originalRotation,originalX,originalY)

        // Check if it overlaps on mirroring
        if (Utility.isOverlappingEnabled.get() && checkOverlappingCondition(
                CollisionUtil.getViewBitmap((imageView?.parent as View))
                    .get(),
                (imageView?.parent as View),
                (imageView?.parent as View).x.toInt(),
                (imageView?.parent as View).y.toInt()
            )
        ) {
            (imageView?.parent as View).rotation = originalRotation
            (imageView?.parent as View).x = originalX
            (imageView?.parent as View).y = originalY
            mOnWorkspaceImageDragListener?.onOverlapped(true)
            return
        }
        (addedViews.find { it.tag == imageView?.tag }) to (imageView?.parent as View)
        addedViewsModel.find { it.id == imageView?.tag }?.xcoordinate =
            (imageView?.parent as View).x ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.ycoordinate =
            (imageView?.parent as View).y ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.rotationAngle =
            (imageView?.parent as View).rotation ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.pivotX =
            (imageView?.parent as View).pivotX ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.pivotY =
            (imageView?.parent as View).pivotY ?: 0F
        addedViewsModel.find { it.id == imageView?.tag }?.transformD= if(addedViewsModel.find { it.id == imageView?.tag }?.isMirrorV == true) "-1.0" else "1.0"

        imageView?.let { mOnWorkspaceImageDragListener?.onDragCompleted() }
    }

    /**
     * Check if Piece is outside Workspace while rotation
     */
    private fun checkRotateOutOfWorkspace(originalRotation: Float, originalX: Float, originalY: Float) {
        val viewParent = (imageView?.parent as View).parent as View
        val viewWidthMiddle = ((imageView?.parent as View).width.toFloat() / 2)
        val viewHeightMiddle = ((imageView?.parent as View).height.toFloat() / 2)
        val parentHeight = (viewParent.height).toFloat()
        val parentWidth = (viewParent.width).toFloat()
        val xMax = (parentWidth - (imageView?.parent as View).width.toFloat())
        val yMax = (parentHeight - (imageView?.parent as View).height.toFloat())
        var xMaxPoint =
            (viewWidthMiddle * Math.abs(cos(Math.toRadians((imageView?.parent as View).rotation.toDouble()))) + viewHeightMiddle * Math.abs(
                sin(
                    Math.toRadians((imageView?.parent as View).rotation.toDouble())
                )
            )).toFloat()
        var yMaxPoint =
            (viewHeightMiddle * Math.abs(cos(Math.toRadians((imageView?.parent as View).rotation.toDouble()))) + viewWidthMiddle * Math.abs(
                sin(
                    Math.toRadians((imageView?.parent as View).rotation.toDouble())
                )
            )).toFloat()
        var xDiff = (viewWidthMiddle - xMaxPoint)
        var yDiff = (viewHeightMiddle - yMaxPoint)

        if (((imageView?.parent as View).x < 0F - xDiff ||
                    (imageView?.parent as View).x > xMax.toFloat() + xDiff ||
                    (imageView?.parent as View).y < 0F - yDiff ||
                    (imageView?.parent as View).y > yMax.toFloat() + yDiff)
        ) {
            Utility.onDrag.set(false)
            (imageView?.parent as View).rotation = originalRotation
            (imageView?.parent as View).x = originalX
            (imageView?.parent as View).y = originalY
            mOnWorkspaceImageDragListener?.onRotationOutOfWorkspace()
            Log.d("TAG", "Rotation Outside Workspace : ")
        }
    }

    fun removePattern(workspacedata: WorkspaceItems?, isSingleDelete: Boolean) {
        if (isSingleDelete) {
            for (i in (addedViews.size - 1) downTo 0) {
                if (addedViews[i].id == workspacedata?.id) {
                    parentView?.removeView(parentView.findViewWithTag(workspacedata.id))
                    addedViews.removeAt(i)
                    addedViewsModel.removeAt(i)
                    parentView?.invalidate()
                    break
                }
            }
        } else {
            for (i in (addedViewsModel.size - 1) downTo 0) {
                if (addedViewsModel[i].parentPatternId == workspacedata?.parentPatternId) {
                    parentView?.removeView(parentView.findViewWithTag(addedViewsModel[i].id))
                    addedViews.removeAt(i)
                    addedViewsModel.removeAt(i)
                    parentView?.invalidate()
                }
            }
        }
    }

    /**
     * Check if Workspace is Empty
     * @return true if Workspace is Empty
     */
    val isWorkspaceNotEmpty: Boolean
        get() = addedViews.size != 0

    /**
     * Builder pattern to define [WorkspaceEditor] Instance
     */
    class Builder(val context: Context, val parentView: WorkspaceEditorView) {
        val imageView: ImageView?

        /**
         * @return build PhotoEditor instance
         */
        fun build(): WorkspaceEditor {
            return WorkspaceEditor(this)
        }

        init {
            imageView = parentView.source
        }
    }

    companion object {
        private const val TAG = "WORKSPACE_EDITOR"
    }

    init {
        context = builder.context
        parentView = builder.parentView
        imageView = builder.imageView
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addedViews = ArrayList()
        redoViews = ArrayList()
        addedViewsModel = ArrayList()
        redoViewsModel = ArrayList()
    }
}