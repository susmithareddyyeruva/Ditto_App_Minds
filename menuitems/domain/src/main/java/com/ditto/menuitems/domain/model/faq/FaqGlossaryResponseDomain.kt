package com.ditto.menuitems.domain.model.faq

data class FaqGlossaryResponseDomain(
    var fAQ: List<FAQDomain>?= emptyList(),
    var glossary: List<GlossaryDomain>?= emptyList(),
    var videos: List<VideosDomain>?= emptyList()
)