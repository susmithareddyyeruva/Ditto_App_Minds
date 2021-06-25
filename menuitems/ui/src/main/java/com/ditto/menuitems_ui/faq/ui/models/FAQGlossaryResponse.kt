package com.ditto.menuitems_ui.faq.ui.models

import com.google.gson.annotations.SerializedName

class FAQGlossaryResponse {
    @SerializedName("FAQ")
    var fAQ: List<FAQModel>?=null
    @SerializedName("Glossary")
    var glossary: List<FAQModel>?=null
}