package com.ditto.mylibrary.model

import com.google.gson.annotations.SerializedName

data class FoldersResult(
    @SerializedName("action")
    var action: String,
    @SerializedName("locale")
    var locale: String,
    @SerializedName("queryString")
    var queryString: String,
    @SerializedName("responseStatus")
    var responseStatus: List<String>
)
