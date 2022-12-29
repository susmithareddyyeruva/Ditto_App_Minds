package com.ditto.storage.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "offline_pattern_data")
data class OfflinePatterns(

    // tailernova response
    @PrimaryKey
    @ColumnInfo(name = "designId")
    @SerializedName("designId")
    @NotNull
    var designId: String,

    //getWorkspace data response

    @ColumnInfo(name = "custId")
    @SerializedName("custId")
    var custId: String?,

    @ColumnInfo(name = "tailornaovaDesignId")
    @SerializedName("tailornaovaDesignId")
    var tailornaovaDesignId: String,

    @ColumnInfo(name = "tailornovaDesignName")
    @SerializedName("tailornovaDesignName")
    var tailornovaDesignName: String?="",


    @ColumnInfo(name = "prodSize")
    @SerializedName("prodSize")
    var prodSize: String?="",

    @ColumnInfo(name = "selectedTab")
    @SerializedName("selectedTab")
    var selectedTab: String? = "",

    @ColumnInfo(name = "status")
    @SerializedName("status")
    var status: String? = "",

    @ColumnInfo(name = "numberOfCompletedPiece")
    @SerializedName("numberOfCompletedPiece")
    var numberOfCompletedPieces: NumberOfCompletedPiecesOffline?,

    @ColumnInfo(name = "patternPieces")
    @SerializedName("patternPieces")
    var patternPiecesFromApi: List<PatternPiecesOffline> = emptyList(),

    @ColumnInfo(name = "garmetWorkspaceItems")
    @SerializedName("garmetWorkspaceItems")
    var garmetWorkspaceItemOfflines: MutableList<WorkspaceItemOffline> = ArrayList(),

    @ColumnInfo(name = "liningWorkspaceItems")
    @SerializedName("liningWorkspaceItems")
    var liningWorkspaceItemOfflines: MutableList<WorkspaceItemOffline> = ArrayList(),

    @ColumnInfo(name = "interfaceWorkspaceItems")
    @SerializedName("interfaceWorkspaceItems")
    var interfaceWorkspaceItemOfflines: MutableList<WorkspaceItemOffline> = ArrayList(),

    @ColumnInfo(name = "otherWorkspaceItems")
    @SerializedName("otherWorkspaceItems")
    var otherWorkspaceItemOfflines: MutableList<WorkspaceItemOffline> = ArrayList(),

    @ColumnInfo(name = "patternName")
    @SerializedName("patternName")
    var patternName: String? = "",

    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String? = "",

    @ColumnInfo(name = "patternType")
    @SerializedName("patternType")
    var patternType: String? = "",

    @ColumnInfo(name = "totalNumberOfPieces")
    @SerializedName("totalNumberOfPieces")
    var numberOfPieces: NumberOfCompletedPiecesOffline?,

    @ColumnInfo(name = "orderModificationDate")
    @SerializedName("orderModificationDate")
    var orderModificationDate: String? = "",

    @ColumnInfo(name = "orderCreationDate")
    @SerializedName("orderCreationDate")
    var orderCreationDate: String? = "",

    @ColumnInfo(name = "instructionFileName")
    @SerializedName("instructionFileName")
    var instructionFileName: String? = "",

    @ColumnInfo(name = "instructionUrl")
    @SerializedName("instructionUrl")
    var instructionUrl: String? = "",

    @ColumnInfo(name = "thumbnailImageUrl")
    @SerializedName("thumbnailImageUrl")
    var thumbnailImageUrl: String? = "",

    @ColumnInfo(name = "thumbnailImageName")
    @SerializedName("thumbnailImageName")
    var thumbnailImageName: String? = "",

    @ColumnInfo(name = "thumbnailEnlargedImageName")
    @SerializedName("thumbnailEnlargedImageName")
    var thumbnailEnlargedImageName: String? = "",

    @ColumnInfo(name = "patternDescriptionImageUrl")
    @SerializedName("patternDescriptionImageUrl")
    var patternDescriptionImageUrl: String? = "",

    @ColumnInfo(name = "selvages")
    @SerializedName("selvages")
    var selvages: List<SelvageData>? = emptyList(),

    @ColumnInfo(name = "patternPiecesTailornova")
    @SerializedName("patternPiecesTailornova")
    var patternPiecesFromTailornova: List<PatternPieceData>? = emptyList(),

    @ColumnInfo(name = "brand")
    @SerializedName("brand")
    var brand: String? = "",

    @ColumnInfo(name = "size")
    @SerializedName("size")
    var size: String? = "",

    @ColumnInfo(name = "gender")
    @SerializedName("gender")
    var gender: String? = "",

    @ColumnInfo(name = "customization")
    @SerializedName("customization")
    var customization: Boolean? = false,

    @ColumnInfo(name = "dressType")
    @SerializedName("dressType")
    var dressType: String? = "",

    @ColumnInfo(name = "suitableFor")
    @SerializedName("suitableFor")
    var suitableFor: String? = "",

    @ColumnInfo(name = "occasion")
    @SerializedName("occasion")
    var occasion: String? = "",

    @ColumnInfo(name = "order_number")
    @SerializedName("order_number")
    var orderNumber: String? = "",

    @ColumnInfo(name = "selectedMannequinId")
    @SerializedName("selectedMannequinId")
    var selectedMannequinId: String? = "",

    @ColumnInfo(name = "selectedMannequinName")
    @SerializedName("selectedMannequinName")
    var selectedMannequinName: String? = "",

    @ColumnInfo(name = "mannequinArray")
    @SerializedName("mannequinArray")
    var mannequin: List<MannequinData>? = emptyList(),

    @ColumnInfo(name = "yardageDetails")
    @SerializedName("yardageDetails")
    var yardageDetails : YardageDetails?,

    @ColumnInfo(name = "lastDateOfModification")
    @SerializedName("lastDateOfModification")
    var lastDateOfModification: String? = "",

    @ColumnInfo(name = "selectedViewCupStyle")
    @SerializedName("selectedViewCupStyle")
    var selectedViewCupStyle: String? = "",

    @ColumnInfo(name = "yardageImageUrl")
    @SerializedName("yardageImageUrl")
    var yardageImageUrl: String? = "",

    @ColumnInfo(name = "yardagePdfUrl")
    @SerializedName("yardagePdfUrl")
    var yardagePdfUrl: String? = "",

    @ColumnInfo(name = "sizeChartUrl")
    @SerializedName("sizeChartUrl")
    var sizeChartUrl: String? = "",

    @ColumnInfo(name = "mainheroImageUrl")
    @SerializedName("mainheroImageUrl")
    var mainheroImageUrl: String? = "",

    @ColumnInfo(name = "heroImageUrls")
    @SerializedName("heroImageUrls")
    var heroImageUrls : HeroImageUrls?
)