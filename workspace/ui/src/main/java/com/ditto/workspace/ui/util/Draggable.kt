package com.ditto.workspace.ui.util

object Draggable {
    enum class STICKY {
        NONE,
        AXIS_X_START,
        AXIS_X_END,
        AXIS_Y_START,
        AXIS_Y_END
    }

    const val SELECT_ALL_TOLERANCE = 5
    const val DRAG_TOLERANCE = 16
    const val DRAG_MOVE_TOLERANCE = 50
    const val DURATION_MILLIS = 100L
    const val SELECT_TO_WORKSPACE = -102
    const val DRAG_OUT_WORKSPACE = -101
    const val SELECT_ALL = -100
}