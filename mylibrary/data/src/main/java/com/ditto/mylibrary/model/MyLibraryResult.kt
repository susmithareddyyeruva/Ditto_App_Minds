package com.ditto.mylibrary.model


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
    @SerializedName("totalCount")
    val totalCount: Int,
    @SerializedName("filter")
    val menu: HashMap<String,List<String>>
)