package com.ditto.onboarding.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.onboarding.domain.model.OnBoardingResultDomain
import com.ditto.onboarding.domain.model.OnboardingData
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface OnboardingRepository {
    fun getOnboardingData(): Single<Result<List<OnboardingData>>>
    fun getOnboardingContent(): Single<Result<OnBoardingResultDomain>>
    fun getUserData(): Single<Result<LoginUser>>
    fun updateDontShowThisScreen(id : Int,dndOnboarding: Boolean,isLaterClicked: Boolean,isWifiLaterClicked: Boolean): Single<Any>
}