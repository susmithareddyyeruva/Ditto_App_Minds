package com.ditto.mylibrary.domain.model

data class AddFavouriteResult(
    var action: String,
    var locale: String,
    var queryString: String,
    var responseStatus: Boolean
)