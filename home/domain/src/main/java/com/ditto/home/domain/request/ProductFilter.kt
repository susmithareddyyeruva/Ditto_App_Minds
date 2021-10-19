package com.ditto.home.domain.request

data class ProductFilter(
    var brand: ArrayList<String>?= arrayListOf()
)