package com.ditto.mylibrary.domain.model

import java.io.Serializable
import java.util.*

data class AllPatternsDomain(
    var action: String?="",
    var locale: String?="",
    var prod: List<ProdDomain>,
    var queryString: String?="",
    val totalPatternCount: Int?=0,
    val totalPageCount: Int?=0,
    val currentPageId: Int?=0,
    val menuItem: HashMap<String,List<String>>?= hashMapOf(),
    val errorMsg:String

):Serializable