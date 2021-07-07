package com.ditto.mylibrary.model


import com.google.gson.annotations.SerializedName

data class Prod(
    @SerializedName("ID") val iD : String="",
    @SerializedName("image") val image : String="",
    @SerializedName("prodName") val prodName : String="",
    @SerializedName("longDescription") val longDescription : String="",
    @SerializedName("creationDate") val creationDate : String="",
    @SerializedName("patternType") val patternType : String="",
    @SerializedName("status") val status : String="",
    @SerializedName("subscriptionExpiryDate") val subscriptionExpiryDate : String="",
    @SerializedName("customization") val customization : String="",
    @SerializedName("dateOfModification") val dateOfModification : String="",
    @SerializedName("type") val type : String="",
    @SerializedName("season") val season : String="",
    @SerializedName("occasion") val occasion : String="",
    @SerializedName("suitableFor") val suitableFor : String="",
    @SerializedName("tailornovaDesignId") val tailornovaDesignId : String="",
    @SerializedName("prodSize") val prodSize : String="",
    @SerializedName("prodGender") val prodGender : String="",
    @SerializedName("prodBrand") val prodBrand : String=""
)