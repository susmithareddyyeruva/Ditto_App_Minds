package com.ditto.menuitems_ui.faq.ui.models

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FAQModel(
    @SerializedName("Ques")
    var question: String? = null,
    @SerializedName("Answ")
    var answer: String? = null,
    @SerializedName("SubAnsw")
    var subAnswer: List<SubAnswModel>? = null,
    @SerializedName("is_expanded")
    var isExpanded: Boolean? = false,
    @SerializedName("web_url")
    var webUrl: String? = null,
    @SerializedName("video_url")
    var videoUrl: String? = null
) : Serializable {
    override
    fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}