package com.ditto.mylibrary.domain.model

data class ProdDomain(
    val iD: Int = 0,
    val image: String = "",
    val prodName: String = "",
    val longDescription: String = "",
    val creationDate: String = "",
    val patternType: String = "",
    val status: String = "",
    val subscriptionExpiryDate: String = "",
    val customization: String = "",
    val dateOfModification: String = "",
    val type: String = "",
    val season: String = "",
    val occasion: String = "",
    val suitableFor: String = "",
    val tailornovaDesignId: String = "",
    val prodSize: String = "",
    val prodGender: String = "",
    val prodBrand: String = ""
)