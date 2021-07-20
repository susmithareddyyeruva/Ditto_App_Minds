package com.ditto.mylibrary.domain.request

data class MyLibraryFilterRequestData(
    var OrderFilter: OrderFilter,
    var ProductFilter: HashMap<String, ArrayList<String>>? = HashMap(),
    var pageId: Int,
    var patternsPerPage:Int
)