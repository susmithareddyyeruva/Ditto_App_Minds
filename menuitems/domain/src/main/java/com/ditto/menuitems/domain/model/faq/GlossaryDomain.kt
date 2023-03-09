package com.ditto.menuitems.domain.model.faq

data class GlossaryDomain(
    var answer: String,
    var question: String,
    var sunAnswer: List<SubAnswDomain>,
    var videoUrl: String,
    var webUrl: String,
    var isExpanded: Boolean? = false
) : java.io.Serializable