package com.ditto.mylibrary.domain.model



data class PatternIdData(
    val brand: String? = "",
    val customization: Boolean? = false,
    var description: String? = "",
    val patternType: String? = "",
    val designId: String,
    val tailornovaDesignName: String?,
    val dressType: String? = "",
    val gender: String? = "",
    val instructionFileName: String? = "",
    val instructionUrl: String? = "",
    var patternName: String? = "",
    val numberOfPieces: NumberOfPiecesData?,
    val occasion: String? = "",
    val orderCreationDate: String? = "",
    val orderModificationDate: String? = "",
    val patternDescriptionImageUrl: String? = "",
    val patternPieces: List<PatternPieceData>? = emptyList(),
    val season: String? = "",
    val selvages: List<SelvageData>? = emptyList(),
    val size: String? = "",
    val suitableFor: String? = "",
    val thumbnailEnlargedImageName: String? = "",
    val thumbnailImageName: String? = "",
    var thumbnailImageUrl: String? = "",
    var selectedMannequinId: String? = "",
    var selectedMannequinName: String? = "",
    var mannequin:List<MannequinData>?= emptyList(),
    var yardageDetails : List<String>? = emptyList(),
    var notionDetails : String?="",
    var lastDateOfModification: String?,
    var selectedViewCupStyle: String?,
    var yardageImageUrl: String?,
    var yardagePdfUrl: String?,
    val mainheroImageUrl: String?,
    val sizeChartUrl: String?,
    val heroImageUrls: List<String> = emptyList()
)

data class NumberOfPiecesData(
    val garment: Int?,
    val `interface`: Int?,
    val lining: Int?,
    val other: Int?
)
data class MannequinData(
    val mannequinId: String? = "",
    val mannequinName: String? = ""
)
data class PatternPieceData(
    val cutOnFold: Boolean,
    val cutQuantity: String = "",
    val pieceDescription: String? = "",
    val id: Int,
    val imageName: String? = "",
    val imageUrl: String? = "",
    val thumbnailImageUrl: String? = "",
    val thumbnailImageName: String? = "",
    val isSpliced: Boolean,
    val mirrorOption: Boolean? = false,
    val pieceNumber: String? = "",
    val positionInTab: String? = "",
    val size: String? = "",
    //var spliceDirection: String? = "",
    val spliceScreenQuantity: String? = "",
    val splicedImages: List<SplicedImageData>? = emptyList(),
    val tabCategory: String? = "",
    val view: String? = "",
    val contrast: String? = ""
)

data class SelvageData(
    val fabricLength: String? = "",
    val id: Int,
    val imageName: String? = "",
    val imageUrl: String? = "",
    val tabCategory: String? = ""
)

data class SplicedImageData(
    val column: Int,
    val designId: String? = "",
    val id: Int,
    val imageName: String? = "",
    val imageUrl: String? = "",
    val mapImageName: String? = "",
    val mapImageUrl: String? = "",
    val pieceId: Int,
    val row: Int
)

