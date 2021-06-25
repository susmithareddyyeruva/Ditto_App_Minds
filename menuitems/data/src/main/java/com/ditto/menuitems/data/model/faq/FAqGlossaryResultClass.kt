package com.ditto.menuitems.data.model.faq


import com.google.gson.annotations.SerializedName

data class FAqGlossaryResultClass(
    @SerializedName("c_body")
    val faqGlossaryResponse: FaqGlossaryResponse,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("_type")
    val type: String,
    @SerializedName("_v")
    val v: String
)