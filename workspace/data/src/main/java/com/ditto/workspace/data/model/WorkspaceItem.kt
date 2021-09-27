package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class WorkspaceItem(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("patternPiecesId")
    val patternPiecesId: Int = 0,
    @SerializedName("isCompleted")
    val isCompleted: Boolean = false,
    @SerializedName("xcoordinate")
    val xcoordinate:Float= 0.0f,
    @SerializedName("ycoordinate")
    val ycoordinate:Float= 0.0f,
    @SerializedName("pivotX")
    val pivotX: Float = 0.0f,
    @SerializedName("pivotY")
    val pivotY:Float= 0.0f,
    @SerializedName("transformA")
    val transformA: String? = "",
    @SerializedName("transformD")
    val transformD: String? = "",
    @SerializedName("rotationAngle")
    val rotationAngle:Float= 0.0f,
    @SerializedName("isMirrorH")
    val isMirrorH: Boolean = false,
    @SerializedName("isMirrorV")
    val isMirrorV: Boolean = false,
    @SerializedName("showMirrorDialog")
    val showMirrorDialog: Boolean = false,
    @SerializedName("currentSplicedPieceNo")
    val currentSplicedPieceNo: String? = ""
)