package com.ditto.mylibrary.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MyLibraryResult(
    @SerializedName("action")
    val action: String?="",
    @SerializedName("locale")
    val locale: String?="",
    @SerializedName("prod")
    val prod: List<Prod>?= emptyList(),
    @SerializedName("queryString")
    val queryString: String?="",
    @SerializedName("totalPatternCount")
    val totalPatternCount: Int?=0,
    @SerializedName("totalPageCount")
    val totalPageCount: Int?=0,
    @SerializedName("currentPageId")
    val currentPageId: Int?=0,
    @SerializedName("filter")
    val menu: HashMap<String,List<String>>?= hashMapOf()
): Serializable