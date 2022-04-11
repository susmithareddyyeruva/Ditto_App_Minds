package com.ditto.storage.data.model

data class WorkspaceItemOffline(
    val id: Int = 0,
    val patternPiecesId: Int = 0,
    val isCompleted: Boolean = false,
    val xcoordinate: Float = 0.0f,
    val ycoordinate: Float = 0.0f,
    val pivotX: Float = 0.0f,
    val pivotY: Float = 0.0f,
    val transformA: String? = "1.0",
    val transformD: String? = "1.0",
    val rotationAngle: Float = 0.0f,
    val isMirrorH: Boolean = false,
    val isMirrorV: Boolean = false,
    val showMirrorDialog: Boolean = false,
    val currentSplicedPieceNo: String? = "",
    var currentSplicedPieceRow:Int = 0,
    var currentSplicedPieceColumn:Int = 0
)