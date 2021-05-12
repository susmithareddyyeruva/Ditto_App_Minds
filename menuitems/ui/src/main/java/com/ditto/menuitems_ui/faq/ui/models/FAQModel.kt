package com.ditto.menuitems_ui.faq.ui.models

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FAQModel (
    @SerializedName("Ques")
    var Ques: String? = null,
    @SerializedName("Answ")
    var Answ: String? = null,
    @SerializedName("SubAnsw")
    var SubAnsw: List<SubAnswModel>? = null,
    @SerializedName("is_expanded")
    var isExpanded: Boolean? = false
    ): Serializable {
    override
    fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}