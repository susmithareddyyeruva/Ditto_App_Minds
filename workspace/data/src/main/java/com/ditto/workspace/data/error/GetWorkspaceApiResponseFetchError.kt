package com.ditto.workspace.data.error

import com.google.gson.annotations.SerializedName

data class GetWorkspaceApiResponseFetchError (
    @SerializedName("fault")
    val fault: Fault,
    @SerializedName("_v")
    val v:String
)

