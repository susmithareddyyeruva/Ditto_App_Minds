package com.ditto.menuitems.data.model.faq


import com.google.gson.annotations.SerializedName

data class FaqGlossaryResponse(
    @SerializedName("FAQ")
    val fAQ: List<FAQ>? = null,
    @SerializedName("Glossary")
    val glossary: List<Glossary>? = null,
    @SerializedName("Videos")
    val videos: List<Videos>? = null
)