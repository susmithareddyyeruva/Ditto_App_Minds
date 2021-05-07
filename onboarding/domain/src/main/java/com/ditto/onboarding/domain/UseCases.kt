package com.ditto.onboarding.domain

import com.ditto.login.domain.LoginUser
import com.ditto.onboarding.domain.model.OnboardingData
import io.reactivex.Single
import non_core.lib.Result

interface GetOnboardingData {
    fun invoke() : Single<Result<List<OnboardingData>>>
    fun invokeOnboardingContent() : Single<Result<OnBoardingResult>>
    fun getUser(): Single<Result<LoginUser>>
    fun updateDontShowThisScreen( id : Int,dndOnboarding: Boolean,isLaterClicked: Boolean,isWifiLaterClicked: Boolean): Single<Any>
}

