package com.ditto.mylibrary.domain.model

data class MenuDomain(
    val category: List<String>,
    val brand: List<String>,
    val size: List<Int>,
    val gender: List<String>
)