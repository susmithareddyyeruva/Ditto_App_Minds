package com.ditto.login.data.model


import com.google.gson.annotations.SerializedName

data class CBody(
    @SerializedName("customerCareEmail")
    val customerCareEmail: String,
    @SerializedName("customerCareePhone")
    val customerCareePhone: String,
    @SerializedName("customerCareeTiming")
    val customerCareeTiming: String,
    @SerializedName("imageUrl")
    val imageUrl: List<String>,
    @SerializedName("videoUrl")
    val videoUrl: String
)