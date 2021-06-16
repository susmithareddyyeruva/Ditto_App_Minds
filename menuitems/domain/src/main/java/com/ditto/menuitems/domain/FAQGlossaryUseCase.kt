package com.ditto.menuitems.domain

import com.ditto.menuitems.domain.model.faq.FAQGlossaryResultDomain
import io.reactivex.Single
import non_core.lib.Result

interface FAQGlossaryUseCase {
    fun getFAQGlossaryDetails(): Single<Result<FAQGlossaryResultDomain>>
}