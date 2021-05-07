package com.ditto.onboarding.domain.model

data class InstructionDomain(
    val description: String?,
    val id: Int?,
    val imagePath: String?,
    val title: String?,
    val videoPath: String?,
    val instructions: List<InstructionXDomain>?,
)