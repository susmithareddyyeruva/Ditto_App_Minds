package com.ditto.onboarding.data.di

import com.ditto.onboarding.data.api.OnBoardingService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class OnboardingApiModule {
    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit): OnBoardingService {
        return retrofit.create(OnBoardingService::class.java)
    }
}