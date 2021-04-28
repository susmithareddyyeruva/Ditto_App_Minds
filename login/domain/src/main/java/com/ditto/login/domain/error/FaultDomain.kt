package com.ditto.login.domain.error


data class FaultDomain(
    val arguments: ArgumentsDomain,
    val message: String,
    val type: String
)