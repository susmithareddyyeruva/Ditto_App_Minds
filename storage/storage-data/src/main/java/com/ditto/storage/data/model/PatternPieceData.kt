package com.ditto.storage.data.model

data class PatternPieceData (
    val cutOnFold: Boolean,
    val cutQuantity: String,
    val description: String,
    val id: Int,
    val imageName: String,
    val imageUrl: String,
    val isSpliced: Boolean,
    val pieceNumber: String,
    val positionInTab: String,
    val size: String,
    val spliceDirection: String,
    val spliceScreenQuantity: String,
    val splicedImages: List<SplicedImageData>,
    val tabCategory: String,
    val view: String
)