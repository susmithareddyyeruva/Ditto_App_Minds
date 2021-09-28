package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class WSUpdateResult(
    @SerializedName("_v")
    val version: String?,
    @SerializedName("_type")
    val type: String?,
    @SerializedName("key_property")
    val key_property: String?,
    @SerializedName("_resource_state")
    val resource_state: String?,
    @SerializedName("key_value_string")
    val key_value_string: String?,
    @SerializedName("object_type")
    val object_type: String?,
    @SerializedName("c_traceWorkSpacePattern")
    val c_traceWorkSpacePattern: String
)