package com.ditto.mylibrary.domain.model

import com.google.gson.annotations.SerializedName

class FilterCriteria {
    @SerializedName("Category")
    var category:String?=null
    @SerializedName("Brand")
    var brand:String?=null
    @SerializedName("Gender")
    var gender:String?=null

}