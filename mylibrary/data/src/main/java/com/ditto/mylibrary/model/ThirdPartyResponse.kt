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
    @SerializedName("name")
    val name: String?,
    @SerializedName("brand")
    val brand: String?,
    @SerializedName("details")
    val details: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("notionsDetails")
    val notionsDetails: String?,
    @SerializedName("yardageDetails")
    val yardageDetails: List<String>? = emptyList(),
    @SerializedName("yardageImageUrl")
    val yardageImageUrl: String?,
    @SerializedName("yardagePdfUrl")
    val yardagePdfUrl: String?
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