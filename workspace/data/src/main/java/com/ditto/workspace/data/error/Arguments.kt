package com.ditto.workspace.data.error

import com.google.gson.annotations.SerializedName

data class Arguments (
    @SerializedName("key")
    val key:String,
    @SerializedName("objectType")
    val objectType: String
)

