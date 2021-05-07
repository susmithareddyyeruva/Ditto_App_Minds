package com.ditto.onboarding.data.model


import com.google.gson.annotations.SerializedName

data class Onboarding(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imagePath")
    val imagePath: String,
    @SerializedName("instructions")
    val instructions: List<Instruction>,
    @SerializedName("title")
    val title: String
)