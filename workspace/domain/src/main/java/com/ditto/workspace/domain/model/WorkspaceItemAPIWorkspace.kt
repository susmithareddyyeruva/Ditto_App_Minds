package com.ditto.workspace.domain.model

data class WorkspaceItemAPIWorkspace (
    val id:Int=0,
    val patternPiecesId:Int=0,
    val isCompleted:String?="",
    val xcoordinate:String?="",
    val ycoordinate:String?="",
    val pivotX:String?="",
    val pivotY:String?="",
    val transformA:String?="",
    val transformD:String?="",
    val rotationAngle:String?="",
    val isMirrorH:String?="",
    val isMirrorV:String?="",
    val showMirrorDialog:String?="",
    val currentSplicedPieceNo:String?=""
)