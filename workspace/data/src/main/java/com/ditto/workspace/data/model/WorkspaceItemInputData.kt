package com.ditto.workspace.data.model

data class WorkspaceItemInputData(
    val id:Int,
    val patternPiecesId:Int,
    val isCompleted:String?,
    val xcoordinate:String?,
    val ycoordinate:String?,
    val pivotX:String?,
    val pivotY:String?,
    val transformA:String?,
    val transformD:String?,
    val rotationAngle:String?,
    val isMirrorH:String?,
    val isMirrorV:String?,
    val showMirrorDialog:String?,
    val currentSplicedPieceNo:String?
)