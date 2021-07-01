package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class PatternPiece(
    @SerializedName("id")
    val id: Int,
    @SerializedName("isCompleted")
    val isCompleted: String?
)