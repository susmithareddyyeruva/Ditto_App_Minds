package com.ditto.mylibrary.domain.model

data class MyLibraryDetailsDomain(
    var action: String,
    var locale: String,
    var prod: List<ProdDomain>,
    var queryString: String
)