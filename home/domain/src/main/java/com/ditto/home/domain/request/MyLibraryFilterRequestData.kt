package com.ditto.home.domain.request

data class MyLibraryFilterRequestData(
    var OrderFilter: OrderFilter,
    var ProductFilter: HashMap<String, ArrayList<String>>? = HashMap()
)