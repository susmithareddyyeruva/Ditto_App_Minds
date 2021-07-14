package com.ditto.mylibrary.model

import com.google.gson.annotations.SerializedName

data class MenuItem(

    @SerializedName("category") val category: List<String>,
    @SerializedName("brand") val brand: List<String>,
    @SerializedName("size") val size: List<Int>,
    @SerializedName("gender") val gender: List<String>
)