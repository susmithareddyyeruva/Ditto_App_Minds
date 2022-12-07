package com.ditto.mylibrary.model

import com.google.gson.annotations.SerializedName


data class ThirdPartyResponse(
    @SerializedName("action")
    val action: String?,
    @SerializedName("locale")
    val locale: String?,
    @SerializedName("product")
    val product: Product?,
    @SerializedName("queryString")
    val queryString: String?,
    @SerializedName("errorMsg")
    val errorMsg:String?
)

data class Product(
    @SerializedName("brandVariantData")
    val brandVariantData: BrandVariantData?,
)

data class BrandVariantData(
    @SerializedName("variation")
    val variation: List<Variation>?,
)

data class Variation(
    @SerializedName("size")
    val size: List<Size>?,
    @SerializedName("style")
    val style: String?,
    @SerializedName("styleName")
    val styleName: String?,
)

data class Size(
    @SerializedName("designID")
    val designID: String?,
    @SerializedName("mannequinID")
    val mannequinID: String?,
    @SerializedName("size")
    val size: String?,
)