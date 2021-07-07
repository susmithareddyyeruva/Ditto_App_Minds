package com.ditto.storage.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "workspace_data")
data class WorkspaceData(

    //getWorkspace data response
    @PrimaryKey
    @ColumnInfo(name="tailornaovaDesignId")
    @SerializedName("tailornaovaDesignId")
    var tailornaovaDesignId: Int? = 0,

    @ColumnInfo(name="selectedTab")
    @SerializedName("selectedTab")
    var selectedTab: String? ="",

    @ColumnInfo(name="status")
    @SerializedName("status")
    var status:String="",

    @ColumnInfo(name = "numberOfCompletedPieces")
    @SerializedName("numberOfCompletedPieces")
    var numberOfPieces: NumberOfPiecesStorage,

    @ColumnInfo(name = "patternPieces")
    @SerializedName("patternPieces")
    var patternPiecesFromApi: List<PatternPiecesFromApiWorkspcaeData> = emptyList(),

    @ColumnInfo(name = "garmetWorkspaceItems")
    @SerializedName("garmetWorkspaceItems")
    var garmetWorkspaceItems: List<WorkspaceItemAPI> = emptyList(),

    @ColumnInfo(name = "liningWorkspaceItems")
    @SerializedName("liningWorkspaceItems")
    var liningWorkspaceItems: List<WorkspaceItemAPI> = emptyList(),

    @ColumnInfo(name = "interfaceWorkspaceItems")
    @SerializedName("interfaceWorkspaceItems")
    var interfaceWorkspaceItems: List<WorkspaceItemAPI> = emptyList()

    // tailernova response
    /*@PrimaryKey
    @ColumnInfo(name = "designId")
    @SerializedName("designId")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name:String="",

    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description:String="",

    @ColumnInfo(name = "numberOfPieces")
    @SerializedName("numberOfPieces")
    var numberOfPieces:NumberOfPieces,

    @ColumnInfo(name = "orderModificationDate")
    @SerializedName("orderModificationDate")
    var orderModificationDate:Date,

    @ColumnInfo(name = "orderCreationDate")
    @SerializedName("orderCreationDate")
    var orderCreationDate:Date,

    @ColumnInfo(name = "instructionFileName")
    @SerializedName("instructionFileName")
    var instructionFileName:String="",

    @ColumnInfo(name="instructionUrl")
    @SerializedName("instructionUrl")
    var instructionUrl:String="",

    @ColumnInfo(name="thumbnailImageUrl")
    @SerializedName("thumbnailImageUrl")
    var thumbnailImageUrl:String="",

    @ColumnInfo(name="thumbnailImageName")
    @SerializedName("thumbnailImageName")
    var thumbnailImageName:String="",

    @ColumnInfo(name="thumbnailEnlargedImageName")
    @SerializedName("thumbnailEnlargedImageName")
    var thumbnailEnlargedImageName:String="",

    @ColumnInfo(name="patternDescriptionImageUrl")
    @SerializedName("patternDescriptionImageUrl")
    var patternDescriptionImageUrl:String="",

    @ColumnInfo(name = "selvages")
    @SerializedName("selvages")
    var selvages: List<SelvagesApi> = emptyList(),

    @ColumnInfo(name = "patternPieces")
    @SerializedName("patternPieces")
    var patternPieces: List<PatternPiecesAPI> = emptyList(),

    @ColumnInfo(name="brand")
    @SerializedName("brand")
    var brand:String="",

    @ColumnInfo(name="size")
    @SerializedName("size")
    var size:String="",

    @ColumnInfo(name="gender")
    @SerializedName("gender")
    var gender:String="",

    @ColumnInfo(name="customization")
    @SerializedName("customization")
    var customization:Boolean=false,

    @ColumnInfo(name="dressType")
    @SerializedName("dressType")
    var dressType:String="",

    @ColumnInfo(name="suitableFor")
    @SerializedName("suitableFor")
    var suitableFor:String="",

    @ColumnInfo(name="occasion")
    @SerializedName("occasion")
    var occasion:String=""*/
)