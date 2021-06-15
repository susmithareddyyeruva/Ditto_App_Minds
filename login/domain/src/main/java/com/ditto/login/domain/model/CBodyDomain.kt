package com.ditto.login.domain.model

data class CBodyDomain(
    var customerCareEmail: String,
    var customerCareePhone: String,
    var customerCareeTiming: String,
    var imageUrl: List<Any>,
    var videoUrl: String
)