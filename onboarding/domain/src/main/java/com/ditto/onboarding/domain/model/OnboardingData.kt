package com.ditto.onboarding.domain.model

data class OnboardingData(
    val id: Int,
    var title: String,
    val description: String,
    var image: String
)