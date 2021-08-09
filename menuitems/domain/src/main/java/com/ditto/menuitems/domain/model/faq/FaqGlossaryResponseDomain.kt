package com.ditto.menuitems.domain.model.faq

data class FaqGlossaryResponseDomain(
    var FAQ: List<FAQDomain>?= emptyList(),
    var Glossary: List<GlossaryDomain>?= emptyList(),
    var Videos: List<VideosDomain>?= emptyList()
)