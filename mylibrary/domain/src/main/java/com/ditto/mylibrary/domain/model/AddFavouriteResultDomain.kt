package com.ditto.mylibrary.domain.model

data class AddFavouriteResultDomain(
    var action: String,
    var locale: String,
    var queryString: String,
    var responseStatus: Boolean
)