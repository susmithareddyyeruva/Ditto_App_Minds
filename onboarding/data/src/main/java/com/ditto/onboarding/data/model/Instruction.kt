package com.ditto.onboarding.data.model


import com.google.gson.annotations.SerializedName

data class Instruction(
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("imagePath")
    val imagePath: String?,
    @SerializedName("instructions")
    val instructions: List<InstructionX>?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("videoPath")
    val videoPath: String?
)