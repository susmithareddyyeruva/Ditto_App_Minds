package com.ditto.workspace.domain.model

data class WorkspaceItemDomain (
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