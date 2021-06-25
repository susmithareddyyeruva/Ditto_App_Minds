package com.ditto.menuitems.data.mapper

import com.ditto.menuitems.data.model.faq.*
import com.ditto.menuitems.domain.model.faq.*

fun FAqGlossaryResultClass.toDomain():FAQGlossaryResultDomain{
    return FAQGlossaryResultDomain(
        id = this.id,
        _type = this.type,
        _v = this.v,
        name = this.name,
        c_body = this.faqGlossaryResponse.toDomain()
    )
}

fun FaqGlossaryResponse.toDomain():FaqGlossaryResponseDomain{
    return FaqGlossaryResponseDomain(
        FAQ = this.fAQ?.map { it.toDomain() },
        Glossary = this.glossary?.map { it.toDomain() }


    )

}

fun FAQ.toDomain():FAQDomain{
    return  FAQDomain(
        Answ= this.answ,
        Ques = this.ques,
        video_url = this.videoUrl,
        web_url = this.webUrl,
        SubAnsw = this.subAnsw.map { it.toDomain() }


    )

}
fun Glossary.toDomain():GlossaryDomain{
    return  GlossaryDomain(
        Answ= this.answ,
        Ques = this.ques,
        video_url = this.videoUrl,
        web_url = this.webUrl,
        SubAnsw = this.subAnsw.map { it.toDomain() }


    )

}
fun SubAnsw.toDomain():SubAnswDomain{
    return SubAnswDomain(
       image_path = this.imagePath,
        short_description = this.shortDescription,
        title = this.title
    )
}