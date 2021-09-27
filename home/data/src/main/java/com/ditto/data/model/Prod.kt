package com.ditto.data.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Prod(
    @SerializedName("ID") val iD : String?="",
    @SerializedName("image") val image : String?="",
    @SerializedName("name") val prodName : String?="",
    @SerializedName("creationDate") val creationDate : String?="",
    @SerializedName("patternType") val patternType : String?="",
    @SerializedName("status") val status : String?="",
    @SerializedName("subscriptionExpiryDate") val subscriptionExpiryDate :String?="",
    @SerializedName("customization") val customization : String?="",
    @SerializedName("dateOfModification") val dateOfModification: String?="",
    @SerializedName("type") val type : String?="",
    @SerializedName("season") val season : String?="",
    @SerializedName("occasion") val occasion : String?="",
    @SerializedName("suitableFor") val suitableFor : String?="",
    @SerializedName("tailornovaDesignId") val tailornovaDesignId : String?="",
    @SerializedName("size") val prodSize : String?="",
    @SerializedName("gender") val prodGender : String?="",
    @SerializedName("brand") val prodBrand : String?=""
): Serializable