package com.ditto.onboarding.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.onboarding.domain.model.OnBoardingResultDomain
import com.ditto.onboarding.domain.model.OnboardingData
import io.reactivex.Single
import non_core.lib.Result

interface GetOnboardingData {
    fun invoke() : Single<Result<List<OnboardingData>>>
    fun invokeOnboardingContent() : Single<Result<OnBoardingResultDomain>>
    fun getUser(): Single<Result<LoginUser>>
    fun updateDontShowThisScreen( id : Int,dndOnboarding: Boolean,isLaterClicked: Boolean,isWifiLaterClicked: Boolean): Single<Any>
}

