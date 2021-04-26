package com.ditto.login.data.error


import com.google.gson.annotations.SerializedName

data class LoginError(
    @SerializedName("fault")
    val fault: Fault,
    @SerializedName("_v")
    val v: String
)