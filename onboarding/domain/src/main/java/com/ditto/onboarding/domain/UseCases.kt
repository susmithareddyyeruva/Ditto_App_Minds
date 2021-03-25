package com.ditto.onboarding.domain

import com.ditto.login.domain.LoginUser
import non_core.lib.Result
import io.reactivex.Single
import com.ditto.onboarding.domain.model.OnboardingData

interface GetOnboardingData {
    fun invoke() : Single<Result<List<OnboardingData>>>
    fun getUser(): Single<Result<LoginUser>>
    fun updateDontShowThisScreen( id : Int,dndOnboarding: Boolean,isLaterClicked: Boolean,isWifiLaterClicked: Boolean): Single<Any>
}

