package com.ditto.mylibrary.data.request

import com.ditto.mylibrary.domain.model.ProductFilter

data class MyLibraryFilterRequestData(
    var OrderFilter: OrderFilter,
    var ProductFilter: ProductFilter
)