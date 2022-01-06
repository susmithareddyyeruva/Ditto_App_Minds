package com.ditto.workspace.domain.model

data class WSUpdateResultDomain(
    val version: String?,
    val type: String?,
    val keyProperty: String?,
    val resourceState: String?,
    val keyValueString: String?,
    val objectType: String?,
    val cTraceworkspacepattern: String
)