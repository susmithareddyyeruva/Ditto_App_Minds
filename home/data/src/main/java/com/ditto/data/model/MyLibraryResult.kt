package com.ditto.data.model


import com.google.gson.annotations.SerializedName

data class MyLibraryResult(
    @SerializedName("action")
    val action: String? = "",
    @SerializedName("locale")
    val locale: String? = "",
    @SerializedName("prod")
    val prod: List<Prod>? = emptyList(),
    @SerializedName("queryString")
    val queryString: String? = "",
    @SerializedName("totalPatternCount")
    val totalPatternCount: Int? = 0,
    @SerializedName("totalPageCount")
    val totalPageCount: Int? = 0,
    @SerializedName("currentPageId")
    val currentPageId: Int?,
    @SerializedName("errorMsg")
    val errorMsg: String?
/*    @SerializedName("filter")
    val filter: HomeFilterMenu*/
)