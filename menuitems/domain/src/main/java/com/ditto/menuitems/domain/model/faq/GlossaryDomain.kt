package com.ditto.menuitems.domain.model.faq

data class GlossaryDomain(
    var Answ: String,
    var Ques: String,
    var SubAnsw: List<SubAnswDomain>,
    var video_url: String,
    var web_url: String,
    var isExpanded: Boolean? = false
)