package com.ditto.menuitems.data.model.faq


import com.google.gson.annotations.SerializedName

data class SubAnsw(
    @SerializedName("image_path")
    val imagePath: String,
    @SerializedName("short_description")
    val shortDescription: String,
    @SerializedName("title")
    val title: String
)