package com.ditto.onboarding.domain.model

data class OnBoardingResultDomain(
    val _type: String,
    val _v: String,
    val c_body: CBodyDomain,
    val id: String,
    val name: String
)