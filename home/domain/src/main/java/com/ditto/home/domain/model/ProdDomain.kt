package com.ditto.home.domain.model

import java.io.Serializable

data class ProdDomain(
    val iD: String = "",
    val image: String = "",
    val name: String = "",
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
):Serializable