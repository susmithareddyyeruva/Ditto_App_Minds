package com.ditto.home.domain.model

data class HomeFilterMenuDomain(
    val category: List<String>,
    val brand: List<String>,
    val size: List<String>,
    val gender: List<String>
)