package com.ditto.onboarding.data.di

import dagger.Binds
import dagger.Module
import com.ditto.onboarding.data.GetOnboardingImpl
import com.ditto.onboarding.data.OnBoardingRepositoryImpl
import com.ditto.onboarding.domain.GetOnboardingData
import com.ditto.onboarding.domain.OnboardingRepository

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [UseCaseModule::class])
interface OnboardingDataModule {
    @Binds
    fun bindonboardingRepository(onBoardingRepositoryImpl: OnBoardingRepositoryImpl): OnboardingRepository
}

@Module
internal interface UseCaseModule {
    @Binds
    fun bindGetOnboardingUseCase(
        getOnboardingImpl: GetOnboardingImpl
    ): GetOnboardingData
}