package com.ditto.home.domain.model

data class MyLibraryDetailsDomain(
    var action: String,
    var locale: String,
    var prod: List<ProdDomain>,
    var queryString: String,
    val totalCount : Int,
    val filter : HomeFilterMenuDomain
)