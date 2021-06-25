package com.ditto.menuitems.data

import com.ditto.menuitems.domain.FAQGlossaryRepository
import com.ditto.menuitems.domain.FAQGlossaryUseCase
import com.ditto.menuitems.domain.model.faq.FAQGlossaryResultDomain
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetFAQGlosaaryUsecaseImpl @Inject constructor(
    private val faqGlossaryRepository: FAQGlossaryRepository
) : FAQGlossaryUseCase {
    override fun getFAQGlossaryDetails():  Single<Result<FAQGlossaryResultDomain>> {
        return  faqGlossaryRepository.getFAQGlosaaryDetails()
    }

}