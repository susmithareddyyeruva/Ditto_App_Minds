package com.ditto.workspace.data.model

import com.google.gson.annotations.SerializedName

data class WSUpdateResult(
    @SerializedName("_v")
    val version: String?,
    @SerializedName("_type")
    val type: String?,
    @SerializedName("key_property")
    val keyProperty: String?,
    @SerializedName("_resource_state")
    val resourceState: String?,
    @SerializedName("key_value_string")
    val keyValueString: String?,
    @SerializedName("object_type")
    val objectType: String?,
    @SerializedName("c_traceWorkSpacePattern")
    val cTraceworkspacepattern: String
)