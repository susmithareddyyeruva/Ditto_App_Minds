package com.ditto.mylibrary.model


import com.google.gson.annotations.SerializedName

data class Prod(
    @SerializedName("ID")
    val iD: String = "",
    @SerializedName("prodBrand")
    val prodBrand: String = "",
    @SerializedName("prodGender")
    val prodGender: String = "",
    @SerializedName("prodName")
    val prodName: String = "",
    @SerializedName("prodPrice")
    val prodPrice: String = "",
    @SerializedName("patternType")
    val patternType: String = "",
    @SerializedName("prodSize")
    val prodSize: String = "",
    @SerializedName("tailornovaDesignId")
    val tailornovaDesignId: String = "",
    @SerializedName("tailornovaDesignName")
    val tailornovaDesignName: String = "",
    @SerializedName("image")
    val image: String = ""
)