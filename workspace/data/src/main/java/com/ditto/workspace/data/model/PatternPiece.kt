package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class PatternPiece(
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("isCompleted")
    val isCompleted: Boolean = false
)