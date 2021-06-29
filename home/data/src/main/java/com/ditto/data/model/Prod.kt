package com.ditto.data.model


import com.google.gson.annotations.SerializedName

data class Prod(
    @SerializedName("hasHighRiskIndicator")
    val hasHighRiskIndicator: Boolean,
    @SerializedName("ID")
    val iD: String,
    @SerializedName("isDiscountApplied")
    val isDiscountApplied: Boolean,
    @SerializedName("isProjectableProduct")
    val isProjectableProduct: Boolean,
    @SerializedName("isTailornovaProduct")
    val isTailornovaProduct: Boolean,
    @SerializedName("prodBrand")
    val prodBrand: String,
    @SerializedName("prodGender")
    val prodGender: String,
    @SerializedName("prodName")
    val prodName: String,
    @SerializedName("prodPrice")
    val prodPrice: String,
    @SerializedName("tailornovaDesignId")
    val tailornovaDesignId: String,
    @SerializedName("tailornovaDesignName")
    val tailornovaDesignName: String
)