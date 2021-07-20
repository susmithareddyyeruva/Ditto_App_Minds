package com.ditto.data.model


import com.google.gson.annotations.SerializedName

data class MyLibraryResult(
    @SerializedName("action")
    val action: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("prod")
    val prod: List<Prod>,
    @SerializedName("queryString")
    val queryString: String,
    @SerializedName("totalPatternCount")
    val totalPatternCount: Int,
    @SerializedName("totalPageCount")
    val totalPageCount: Int,
    @SerializedName("currentPageId")
    val currentPageId: Int,
    @SerializedName("filter")
    val filter: HomeFilterMenu
)