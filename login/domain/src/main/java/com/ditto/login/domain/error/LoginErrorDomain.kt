package com.ditto.login.domain.error


data class LoginErrorDomain(
    val faultDomain: FaultDomain,
    val v: String
)