package com.ditto.mylibrary.domain.model

import java.io.Serializable

data class ProdDomain(
    val iD: String?="",
    val image: String?="",
    val prodName: String?="",
    val description: String?="",
    val creationDate: String?="",
    val patternType:String?="",
    val status: String?="",
    val subscriptionExpiryDate: String?="",
    val customization:String?="",
    val dateOfModification: String?="",
    val type: String?="",
    val season: String?="",
    val occasion: String?="",
    val suitableFor: String?="",
    val tailornovaDesignId: String?="",
    val orderNo: String?="",
    val prodSize: String?="",
    val prodGender: String?="",
    val prodBrand: String?="",
    var isFavourite:Boolean?=false
):Serializable