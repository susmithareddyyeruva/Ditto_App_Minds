package com.ditto.mylibrary.domain.model

data class AllPatternsDomain(
    var action: String,
    var locale: String,
    var prod: List<ProdDomain>,
    var queryString: String,
    val totalCount: Int,
    val menuItem: MenuDomain
)