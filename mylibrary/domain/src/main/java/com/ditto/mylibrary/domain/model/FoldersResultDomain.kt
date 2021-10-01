package com.ditto.mylibrary.domain.model

data class FoldersResultDomain(
    var action: String,
    var locale: String,
    var queryString: String,
    var responseStatus: List<String>,
    val errorMsg: String?
)
