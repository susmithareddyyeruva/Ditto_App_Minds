package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class NumberOfCompletedPiece(
    @SerializedName("garment")
    val garment:Int?,
    @SerializedName("lining")
    val lining:Int?,
    @SerializedName("interface")
    val interfacee:Int?
)