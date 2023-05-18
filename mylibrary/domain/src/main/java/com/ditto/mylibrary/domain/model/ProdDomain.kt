package com.ditto.mylibrary.domain.model

import java.io.Serializable

data class ProdDomain(
    var iD: String? = "",
    var image: String? = "",
    var prodName: String? = "",
    var description: String? = "",
    val creationDate: String? = "",
    val patternType: String? = "",
    val status: String? = "",
    val subscriptionExpiryDate: String? = "",
    val customization: String? = "",
    val dateOfModification: String? = "",
    //val type: String? = "",
    //val season: String? = "",
    val occasion: String? = "",
    val suitableFor: String? = "",
    val tailornovaDesignId: String? = "",
    var tailornovaDesignName: String? = "",
    val orderNo: String? = "",
    var prodSize: String? = "",
    val prodGender: String? = "",
    var prodBrand: String? = "",
    var isFavourite: Boolean? = false,
    var purchasedSizeId:String?="",
    var selectedMannequinId: String? = "",
    var selectedMannequinName:String?="",
    var mannequin:List<MannequinDataDomain>?= emptyList(),
    var yardageDetails : List<String>? = emptyList(),
    var notionDetails : String? = "",
    var customSizeFitName : String? = "",
    var lastModifiedSizeDate : String? = "",
    var yardagePdfUrl : String?=""
) : Serializable