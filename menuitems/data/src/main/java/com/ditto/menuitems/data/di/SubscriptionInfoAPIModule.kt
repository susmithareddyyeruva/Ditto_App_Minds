package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.api.SubscriptionInfoService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class SubscriptionInfoAPIModule {
    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit) : SubscriptionInfoService {
        return retrofit.create(SubscriptionInfoService::class.java)
    }
}