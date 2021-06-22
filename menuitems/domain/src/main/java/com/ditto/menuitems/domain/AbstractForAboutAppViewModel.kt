package com.ditto.menuitems.domain

import com.ditto.menuitems.domain.model.AboutAppDomain
import com.ditto.menuitems.domain.model.AboutAppResponseData
import io.reactivex.Single
import non_core.lib.Result

interface AbstractForAboutAppViewModel {
    fun getAboutAppAndPrivacyData():Single<Result<AboutAppDomain>>
}