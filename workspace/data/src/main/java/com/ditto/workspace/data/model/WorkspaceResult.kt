package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class WorkspaceResult (
    @SerializedName("_v")
    val version: String?,
    @SerializedName("_type")
    val type: String?,
    @SerializedName("key_property")
    val key_property: String?,
    @SerializedName("object_type")
    val object_type: String?,
    @SerializedName("c_traceWorkSpacePattern")
    val c_traceWorkSpacePattern: CTraceWorkSpacePattern
)
