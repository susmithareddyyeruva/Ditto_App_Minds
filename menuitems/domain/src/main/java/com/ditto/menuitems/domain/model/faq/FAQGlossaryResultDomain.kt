package com.ditto.menuitems.domain.model.faq

data class FAQGlossaryResultDomain(
    var _type: String,
    var _v: String,
    var c_body: FaqGlossaryResponseDomain,
    var id: String,
    var name: String
)