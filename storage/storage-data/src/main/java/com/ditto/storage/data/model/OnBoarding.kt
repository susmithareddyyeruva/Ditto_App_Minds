package com.ditto.storage.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Model/Entity class representing OnBoarding data
 */
@Entity(tableName = "onboard_data")
data class OnBoarding(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Int = 0,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String = "",
    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String = "",
    @ColumnInfo(name = "imagePath")
    @SerializedName("imagePath")
    var imagepath: String = "",
    @ColumnInfo(name = "videoPath")
    @SerializedName("videoPath")
    var videoPath: String = "",
    @ColumnInfo(name = "tutorialPdfUrl")
    @SerializedName("tutorialPdfUrl")
    var tutorialPdfUrl: String = "",
    @ColumnInfo(name = "instructions")
    @SerializedName("instructions")
    var instructions: List<Instructions> = emptyList()
)