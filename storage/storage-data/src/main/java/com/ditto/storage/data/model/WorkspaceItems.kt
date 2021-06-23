package com.ditto.storage.data.model

import androidx.room.PrimaryKey

data class WorkspaceItems (
    var id: Int = 0,
    var parentPattern: String = "",
    var imagePath: String = "",
    var size: String = "",
    var view: String = "",
    var pieceNumber: String = "",
    var pieceDescription: String = "",
    var positionInTab: String = "",
    var tabCategory: String = "",
    var cutQuantity: String = "",
    var splice: String = "",
    var spliceDirection: String = "",
    var spliceScreenQuantity: String = "",
    var splicedImages: List<SpliceImages> = emptyList(),
    var cutOnFold: String = "",
    var mirrorOption: String = "",
    var xcoordinate: Float = 0.0f,
    var ycoordinate: Float = 0.0f,
    var pivotX: Float = 0.0f,
    var pivotY: Float = 0.0f,
    var rotationAngle: Float = 0.0f,
    var isMirrorH: Boolean = false,
    var isMirrorV: Boolean = false,
    var parentPatternId: Int = 0,
    var isCompleted: Boolean = false,
    var currentSplicedPieceRow:Int = 0,
    var currentSplicedPieceColumn:Int = 0
)