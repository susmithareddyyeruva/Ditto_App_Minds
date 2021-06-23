package com.ditto.menuitems.domain

import com.ditto.menuitems.domain.model.AboutAppDomain
import io.reactivex.Single
import non_core.lib.Result

interface AboutAppUseCase {
    fun getAboutAppAndPrivacyData(): Single<Result<AboutAppDomain>>
}