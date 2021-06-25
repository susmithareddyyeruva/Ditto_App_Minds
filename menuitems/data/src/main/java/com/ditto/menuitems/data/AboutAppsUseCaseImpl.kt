package com.ditto.menuitems.data

import com.ditto.menuitems.domain.AboutAppRepository
import com.ditto.menuitems.domain.AboutAppUseCase
import com.ditto.menuitems.domain.model.AboutAppDomain
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class AboutAppsUseCaseImpl @Inject constructor(
    private val aboutAppRepository: AboutAppRepository
): AboutAppUseCase {


   override fun getAboutAppAndPrivacyData() : Single<Result<AboutAppDomain>>{
        return aboutAppRepository.getAboutAppAndPrivacyData()
    }

}