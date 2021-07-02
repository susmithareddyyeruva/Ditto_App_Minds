package com.ditto.mylibrary.domain.model

import com.google.gson.annotations.SerializedName

class ProductFilter {
    @SerializedName("Category")
    var category:String?=null
    @SerializedName("brand")
    var brand:String?=null
    @SerializedName("Gender")
    var gender:String?=null
    @SerializedName("Type")
    var type:String?=null
    @SerializedName("Suitable")
    var suitable:String?=null
    @SerializedName("Season")
    var season:String?=null
    @SerializedName("Occasion")
    var occasion:String?=null
    @SerializedName("Size")
    var size:String?=null
    @SerializedName("Customization")
    var customization:String?=null







}