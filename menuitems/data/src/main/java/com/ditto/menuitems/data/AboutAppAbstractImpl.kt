package com.ditto.menuitems.data

import com.ditto.menuitems.domain.AboutAppAbstractRespository
import com.ditto.menuitems.domain.AbstractForAboutAppViewModel
import com.ditto.menuitems.domain.model.AboutAppDomain
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class AboutAppAbstractImpl @Inject constructor(
    private val aboutAppAbstractRespository: AboutAppAbstractRespository
): AbstractForAboutAppViewModel {


   override fun getAboutAppAndPrivacyData() : Single<Result<AboutAppDomain>>{
        return aboutAppAbstractRespository.getAboutAppAndPrivacyData()
    }

}