package com.ditto.onboarding.domain.model

data class OnboardingDomain(
    var description: String?,
    var id: Int?,
    val imagePath: String?,
    var instructions: List<InstructionDomain>?,
    var title: String?
)