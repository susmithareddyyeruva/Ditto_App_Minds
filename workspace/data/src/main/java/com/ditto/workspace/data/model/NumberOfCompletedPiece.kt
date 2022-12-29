package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class NumberOfCompletedPiece(
    @SerializedName("garment")
    val garment: Int? = 0,
    @SerializedName("lining")
    val lining: Int? = 0,
    @SerializedName("interface")
    val `interface`: Int? = 0,
    @SerializedName("other")
    val other: Int? = 0
)