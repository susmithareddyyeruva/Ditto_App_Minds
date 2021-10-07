package com.ditto.storage.data.model

data class PatternPieceData (
    val cutOnFold: Boolean,
    val cutQuantity: String="",
    val pieceDescription: String?="",
    val id: Int,
    val imageName: String?="",
    val imageUrl: String?="",
    val thumbnailImageUrl: String? = "",
    val thumbnailImageName: String? = "",
    val isSpliced: Boolean,
    val pieceNumber: String?="",
    val positionInTab: String?="",
    val size: String?="",
    //val spliceDirection: Int?=0,
    val spliceScreenQuantity: String?="",
    val splicedImages: List<SplicedImageData>,
    val tabCategory: String?="",
    val view: String?=""
)