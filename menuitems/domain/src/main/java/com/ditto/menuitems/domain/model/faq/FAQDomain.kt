package com.ditto.menuitems.domain.model.faq

data class FAQDomain(
    var answer: String,
    var question: String,
    var subAnswer: List<SubAnswDomain>,
    var videoUrl: String,
    var webUrl: String,
    var isExpanded: Boolean? = false
)