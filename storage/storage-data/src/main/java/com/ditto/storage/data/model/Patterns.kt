package com.ditto.storage.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Model/Entity class representing Patterns data
 */
@Entity(tableName = "patterns_data")
data class Patterns(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: String,
    @ColumnInfo(name = "patternName")
    @SerializedName("patternName")
    var patternName: String = "",
    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String = "",
    @ColumnInfo(name = "totalPieces")
    @SerializedName("totalPieces")
    var totalPieces: Int = 0,
    @ColumnInfo(name = "completedPieces")
    @SerializedName("completedPieces")
    var completedPieces: Int = 0,
    @ColumnInfo(name = "selectedTab")
    @SerializedName("selectedTab")
    var selectedTab: String? = "",
    @ColumnInfo(name = "status")
    @SerializedName("status")
    var status: String? = "",
    @ColumnInfo(name = "thumbnailImagePath")
    @SerializedName("thumbnailImagePath")
    var thumbnailImagePath: String = "",
    @ColumnInfo(name = "descriptionImages")
    @SerializedName("descriptionImages")
    var descriptionImages: List<DescriptionImages> = emptyList(),
    @ColumnInfo(name = "selvages")
    @SerializedName("selvages")
    var selvages: List<Selvages> = emptyList(),
    @ColumnInfo(name = "patternPieces")
    @SerializedName("patternPieces")
    var patternPieces: List<PatternPieces> = emptyList(),

    @ColumnInfo(name = "numberOfCompletedPiece")
    @SerializedName("numberOfCompletedPiece")
    var numberOfCompletedPieces: NumberOfCompletedPiecesOffline?,

    @ColumnInfo(name = "numberOfPieces")
    @SerializedName("numberOfPieces")
    var numberOfPieces:NumberOfCompletedPiecesOffline?,

    @ColumnInfo(name="thumbnailImageName")
    @SerializedName("thumbnailImageName")
    var thumbnailImageName:String="",
    /*@ColumnInfo(name = "workspaceItems")
    @SerializedName("workspaceItems")
    var workspaceItems: MutableList<WorkspaceItems>? = ArrayList(),*/
    @ColumnInfo(name = "garmetWorkspaceItems")
    @SerializedName("garmetWorkspaceItems")
    var garmetWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),

    @ColumnInfo(name = "liningWorkspaceItems")
    @SerializedName("liningWorkspaceItems")
    var liningWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),

    @ColumnInfo(name = "interfaceWorkspaceItems")
    @SerializedName("interfaceWorkspaceItems")
    var interfaceWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
)