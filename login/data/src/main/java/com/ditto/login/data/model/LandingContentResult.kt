package com.ditto.login.data.model


import com.google.gson.annotations.SerializedName

data class LandingContentResult(
    @SerializedName("c_body")
    val cBody: CBody,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("_type")
    val type: String,
    @SerializedName("_v")
    val v: String
)