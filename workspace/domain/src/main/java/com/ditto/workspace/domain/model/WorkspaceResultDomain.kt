package com.ditto.workspace.domain.model

data class WorkspaceResultDomain(
    val version: String?,
    val type: String?,
    val key_property: String?,
    val object_type: String?,
    val c_traceWorkSpacePattern: CTraceWorkSpacePatternDomain
)