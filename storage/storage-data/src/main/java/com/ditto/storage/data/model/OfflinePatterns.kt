package com.ditto.storage.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "offline_pattern_data")
data class OfflinePatterns(

    //getWorkspace data response

    @ColumnInfo(name="custId")
    @SerializedName("custId")
    var custId: String?,

    @ColumnInfo(name="tailornaovaDesignId")
    @SerializedName("tailornaovaDesignId")
    var tailornaovaDesignId: String,

    @ColumnInfo(name="selectedTab")
    @SerializedName("selectedTab")
    var selectedTab: String? ="",

    @ColumnInfo(name="status")
    @SerializedName("status")
    var status:String="",

    @ColumnInfo(name = "numberOfCompletedPiece")
    @SerializedName("numberOfCompletedPiece")
    var numberOfCompletedPieces: NumberOfCompletedPiecesOffline,

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

    // tailernova response
    @PrimaryKey
    @ColumnInfo(name = "designId")
    @SerializedName("designId")
    @NotNull
    var designId: String,

    @ColumnInfo(name = "patternName")
    @SerializedName("patternName")
    var patternName:String? ="",

    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description:String?="",

    @ColumnInfo(name = "patternType")
    @SerializedName("patternType")
    var patternType:String?="",

    @ColumnInfo(name = "totalNumberOfPieces")
    @SerializedName("totalNumberOfPieces")
    var numberOfPieces:NumberOfCompletedPiecesOffline,

    @ColumnInfo(name = "orderModificationDate")
    @SerializedName("orderModificationDate")
    var orderModificationDate: String?="",

    @ColumnInfo(name = "orderCreationDate")
    @SerializedName("orderCreationDate")
    var orderCreationDate:String?="",

    @ColumnInfo(name = "instructionFileName")
    @SerializedName("instructionFileName")
    var instructionFileName:String?="",

    @ColumnInfo(name="instructionUrl")
    @SerializedName("instructionUrl")
    var instructionUrl:String?="",

    @ColumnInfo(name="thumbnailImageUrl")
    @SerializedName("thumbnailImageUrl")
    var thumbnailImageUrl:String?="",

    @ColumnInfo(name="thumbnailImageName")
    @SerializedName("thumbnailImageName")
    var thumbnailImageName:String?="",

    @ColumnInfo(name="thumbnailEnlargedImageName")
    @SerializedName("thumbnailEnlargedImageName")
    var thumbnailEnlargedImageName:String?="",

    @ColumnInfo(name="patternDescriptionImageUrl")
    @SerializedName("patternDescriptionImageUrl")
    var patternDescriptionImageUrl:String?="",

    @ColumnInfo(name = "selvages")
    @SerializedName("selvages")
    var selvages: List<SelvageData> = emptyList(),

    @ColumnInfo(name = "patternPiecesTailornova")
    @SerializedName("patternPiecesTailornova")
    var patternPieces: List<PatternPieceData> = emptyList(),

    @ColumnInfo(name="brand")
    @SerializedName("brand")
    var brand:String?="",

    @ColumnInfo(name="size")
    @SerializedName("size")
    var size:Int=0,

    @ColumnInfo(name="gender")
    @SerializedName("gender")
    var gender:String?="",

    @ColumnInfo(name="customization")
    @SerializedName("customization")
    var customization:Boolean?=false,

    @ColumnInfo(name="dressType")
    @SerializedName("dressType")
    var dressType:String?="",

    @ColumnInfo(name="suitableFor")
    @SerializedName("suitableFor")
    var suitableFor:String?="",

    @ColumnInfo(name="occasion")
    @SerializedName("occasion")
    var occasion:String?=""
)