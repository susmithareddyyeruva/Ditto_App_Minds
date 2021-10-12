package com.ditto.mylibrary.domain.model


data class PatternIdData(
    val brand: String? ="",
    val customization: Boolean? = false,
    val description: String? ="",
    val patternType: String? ="",
    val designId: String,
    val dressType: String? ="",
    val gender: String? ="",
    val instructionFileName: String? ="",
    val instructionUrl: String? ="",
    val patternName: String? ="",
    val numberOfPieces: NumberOfPiecesData?,
    val occasion: String? ="",
    val orderCreationDate: String? ="",
    val orderModificationDate: String? ="",
    val patternDescriptionImageUrl: String? ="",
    val patternPieces: List<PatternPieceData>? = emptyList(),
    val season: String? ="",
    val selvages: List<SelvageData>? = emptyList(),
    val size: String? = "",
    val suitableFor: String? ="",
    val thumbnailEnlargedImageName: String? ="",
    val thumbnailImageName: String? ="",
    val thumbnailImageUrl: String? =""
)

data class NumberOfPiecesData(
    val garment: Int?,
    val `interface`: Int?,
    val lining: Int?
)

data class PatternPieceData(
    val cutOnFold: Boolean,
    val cutQuantity: String ="",
    val pieceDescription: String? ="",
    val id: Int,
    val imageName: String? ="",
    val imageUrl: String? ="",
    val thumbnailImageUrl: String? = "",
    val thumbnailImageName: String? = "",
    val isSpliced: Boolean,
    val isMirrorOption: Boolean? = false,
    val pieceNumber: String? ="",
    val positionInTab: String? ="",
    val size: String? ="",
    //var spliceDirection: String? = "",
    val spliceScreenQuantity: String? ="",
    val splicedImages: List<SplicedImageData>? = emptyList(),
    val tabCategory: String? ="",
    val view: String? =""
)

data class SelvageData(
    val fabricLength: String? ="",
    val id: Int,
    val imageName: String? ="",
    val imageUrl: String? ="",
    val tabCategory: String? =""
)

data class SplicedImageData(
    val column: Int,
    val designId: String? ="",
    val id: Int,
    val imageName: String?="",
    val imageUrl: String?="",
    val mapImageName: String?="",
    val mapImageUrl: String?="",
    val pieceId: Int,
    val row: Int
)

