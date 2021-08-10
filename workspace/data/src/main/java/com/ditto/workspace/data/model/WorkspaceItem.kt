package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class WorkspaceItem (
    @SerializedName("id")
    val id:Int,
    @SerializedName("patternPiecesId")
    val patternPiecesId:Int,
    @SerializedName("isCompleted")
    val isCompleted:String?,
    @SerializedName("xcoordinate")
    val xcoordinate:String?,
    @SerializedName("ycoordinate")
    val ycoordinate:String?,
    @SerializedName("pivotX")
    val pivotX:String?,
    @SerializedName("pivotY")
    val pivotY:String?,
    @SerializedName("transformA")
    val transformA:String?,
    @SerializedName("transformD")
    val transformD:String?,
    @SerializedName("rotationAngle")
    val rotationAngle:String?,
    @SerializedName("isMirrorH")
    val isMirrorH:String?,
    @SerializedName("isMirrorV")
    val isMirrorV:String?,
    @SerializedName("showMirrorDialog")
    val showMirrorDialog:String?,
    @SerializedName("currentSplicedPieceNo")
    val currentSplicedPieceNo:String?
)