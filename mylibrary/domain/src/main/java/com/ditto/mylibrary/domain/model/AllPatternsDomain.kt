package com.ditto.mylibrary.domain.model

import java.util.*

data class AllPatternsDomain(
    var action: String,
    var locale: String,
    var prod: List<ProdDomain>,
    var queryString: String,
    val totalCount: Int,
    val menuItem: HashMap<String,List<String>>
)