package com.ditto.login.data.error


import com.google.gson.annotations.SerializedName

data class Arguments(
    @SerializedName("credentialType")
    val credentialType: String
)