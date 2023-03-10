package com.ditto.menuitems.domain.model.faq

data class VideosDomain(
    var answ: String,
    var ques: String,
    var subAnsw: List<SubAnswDomain>,
    var videoUrl: String,
    var webUrl: String,
    var isExpanded: Boolean? = false
): java.io.Serializable