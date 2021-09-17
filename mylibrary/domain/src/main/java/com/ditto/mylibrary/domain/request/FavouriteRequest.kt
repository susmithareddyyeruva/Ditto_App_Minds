package com.ditto.mylibrary.domain.request

import com.ditto.mylibrary.domain.model.ProductFilter

data class FavouriteRequest(
    var OrderFilter: OrderFilter,
    var ProductFilter: ProductFilter,
    var FoldersConfig: HashMap<String,ArrayList<String>>,
)