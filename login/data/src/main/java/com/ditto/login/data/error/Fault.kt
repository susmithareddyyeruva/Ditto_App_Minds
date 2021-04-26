package com.ditto.login.data.error


import com.google.gson.annotations.SerializedName

data class Fault(
    @SerializedName("arguments")
    val arguments: Arguments,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String
)