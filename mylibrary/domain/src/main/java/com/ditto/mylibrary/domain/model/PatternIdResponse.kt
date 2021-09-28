package com.ditto.mylibrary.domain.model
import com.google.gson.annotations.SerializedName


data class PatternIdResponse(
    @SerializedName("brand")
    val brand: String = "",
    @SerializedName("customization")
    val customization: Boolean = false,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("designId")
    val designId: String = "",
    @SerializedName("dressType")
    val dressType: String = "",
    @SerializedName("gender")
    val gender: String = "",
    @SerializedName("instructionFileName")
    val instructionFileName: String = "",
    @SerializedName("instructionUrl")
    val instructionUrl: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("numberOfPieces")
    val numberOfPieces: NumberOfPieces = NumberOfPieces(),
    @SerializedName("occasion")
    val occasion: String = "",
    @SerializedName("orderCreationDate")
    val orderCreationDate: String = "",
    @SerializedName("orderModificationDate")
    val orderModificationDate: String = "",
    @SerializedName("patternDescriptionImageUrl")
    val patternDescriptionImageUrl: String = "",
    @SerializedName("patternPieces")
    val patternPieces: List<PatternPiece> = listOf(),
    @SerializedName("season")
    val season: String = "",
    @SerializedName("selvages")
    val selvages: List<Selvage> = listOf(),
    @SerializedName("size")
    val size: Int = 0,
    @SerializedName("suitableFor")
    val suitableFor: String = "",
    @SerializedName("thumbnailEnlargedImageName")
    val thumbnailEnlargedImageName: String = "",
    @SerializedName("thumbnailImageName")
    val thumbnailImageName: String = "",
    @SerializedName("thumbnailImageUrl")
    val thumbnailImageUrl: String = ""
)

data class NumberOfPieces(
    @SerializedName("garment")
    val garment: Int = 0,
    @SerializedName("interface")
    val interfaceX: Int = 0,
    @SerializedName("lining")
    val lining: Int = 0
)

data class PatternPiece(
    @SerializedName("cutOnFold")
    val cutOnFold: Boolean = false,
    @SerializedName("cutQuantity")
    val cutQuantity: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("imageName")
    val imageName: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("thumbnailImageName")
    val thumbnailImageName: String = "",
    @SerializedName("thumbnailImageUrl")
    val thumbnailImageUrl: String = "",
    @SerializedName("isSpliced")
    val isSpliced: Boolean = false,
    @SerializedName("pieceNumber")
    val pieceNumber: String = "",
    @SerializedName("positionInTab")
    val positionInTab: String = "",
    @SerializedName("size")
    val size: String = "",
    @SerializedName("spliceDirection")
    val spliceDirection: String = "",
    @SerializedName("spliceScreenQuantity")
    val spliceScreenQuantity: String = "",
    @SerializedName("splicedImages")
    val splicedImages: List<SplicedImage> = listOf(),
    @SerializedName("tabCategory")
    val tabCategory: String = "",
    @SerializedName("view")
    val view: String = ""
)

data class Selvage(
    @SerializedName("fabricLength")
    val fabricLength: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("imageName")
    val imageName: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("tabCategory")
    val tabCategory: String = ""
)

data class SplicedImage(
    @SerializedName("column")
    val column: Int = 0,
    @SerializedName("designId")
    val designId: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("imageName")
    val imageName: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("mapImageName")
    val mapImageName: String = "",
    @SerializedName("mapImageUrl")
    val mapImageUrl: String = "",
    @SerializedName("pieceId")
    val pieceId: Int = 0,
    @SerializedName("row")
    val row: Int = 0
)