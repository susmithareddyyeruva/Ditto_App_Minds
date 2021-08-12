package com.ditto.data.model

import com.google.gson.annotations.SerializedName

data class HomeFilterMenu (
    @SerializedName("category") val category : List<String>,
    @SerializedName("brand") val brand : List<String>,
    @SerializedName("size") val size : List<String>,
    @SerializedName("gender") val gender : List<String>
)