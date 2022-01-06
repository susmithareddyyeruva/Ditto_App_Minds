package com.ditto.menuitems.data.mapper

import com.ditto.menuitems.data.model.faq.*
import com.ditto.menuitems.domain.model.faq.*

fun FAqGlossaryResultClass.toDomain():FAQGlossaryResultDomain{
    return FAQGlossaryResultDomain(
        id = this.id,
        _type = this.type,
        _v = this.v,
        name = this.name,
        cBody = this.faqGlossaryResponse.toDomain()
    )
}

fun FaqGlossaryResponse.toDomain():FaqGlossaryResponseDomain{
    return FaqGlossaryResponseDomain(
        fAQ = this.fAQ?.map { it.toDomain() },
        glossary = this.glossary?.map { it.toDomain()},
        videos = this.videos?.map { it.toDomain() }


    )

}

fun FAQ.toDomain():FAQDomain{
    return  FAQDomain(
        answer= this.answ,
        question = this.ques,
        videoUrl = this.videoUrl,
        webUrl = this.webUrl,
        subAnswer = this.subAnsw.map { it.toDomain() }


    )

}
fun Videos.toDomain():VideosDomain{
    return  VideosDomain(
        answ= this.answ,
        ques = this.ques,
        videoUrl = this.videoUrl,
        webUrl = this.webUrl,
        subAnsw = this.subAnsw.map { it.toDomain() }


    )

}
fun Glossary.toDomain():GlossaryDomain{
    return  GlossaryDomain(
        answer= this.answ,
        question = this.ques,
        videoUrl = this.videoUrl,
        webUrl = this.webUrl,
        sunAnswer = this.subAnsw.map { it.toDomain() }


    )

}
fun SubAnsw.toDomain():SubAnswDomain{
    return SubAnswDomain(
       image_path = this.imagePath,
        short_description = this.shortDescription,
        title = this.title
    )
}