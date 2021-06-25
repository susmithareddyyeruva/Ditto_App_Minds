package com.ditto.menuitems.domain.model

import com.google.gson.annotations.SerializedName


data class AboutAppResponseData (
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("c_body")
    val c_body: String
)