package com.ditto.workspace.domain.model

data class WSUpdateResultDomain(
    val version: String?,
    val type: String?,
    val key_property: String?,
    val resource_state: String?,
    val key_value_string: String?,
    val object_type: String?,
    val c_traceWorkSpacePattern: String
)