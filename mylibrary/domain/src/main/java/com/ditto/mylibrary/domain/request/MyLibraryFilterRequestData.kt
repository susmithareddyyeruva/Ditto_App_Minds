package com.ditto.mylibrary.domain.request

data class MyLibraryFilterRequestData(
    var orderFilter: OrderFilter,
    var productFilter: HashMap<String, ArrayList<String>>? = HashMap(),
    var pageId: Int,
    var patternsPerPage:Int,
    var searchTerm:String
)