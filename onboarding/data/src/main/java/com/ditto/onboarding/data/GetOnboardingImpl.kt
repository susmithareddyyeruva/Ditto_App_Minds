package com.ditto.onboarding.data

import com.ditto.login.domain.LoginUser
import com.ditto.onboarding.domain.GetOnboardingData
import com.ditto.onboarding.domain.OnboardingRepository
import com.ditto.onboarding.domain.model.OnBoardingResultDomain
import com.ditto.onboarding.domain.model.OnboardingData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject


class GetOnboardingImpl @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : GetOnboardingData {
    override fun invoke(): Single<Result<List<OnboardingData>>> {
        return onboardingRepository.getOnboardingData()
    }

    override fun invokeOnboardingContent(): Single<Result<OnBoardingResultDomain>> {
        return onboardingRepository.getOnboardingContent()
    }

    override fun getUser(): Single<Result<LoginUser>> {
        return onboardingRepository.getUserData()
    }
    override fun updateDontShowThisScreen(id : Int,dndOnboarding: Boolean,isLaterClicked: Boolean,isWifiLaterCLicked: Boolean): Single<Any> {
        return onboardingRepository.updateDontShowThisScreen(id,dndOnboarding,isLaterClicked,isWifiLaterCLicked)
    }

}