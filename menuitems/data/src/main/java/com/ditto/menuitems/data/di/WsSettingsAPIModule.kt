package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.api.WsSettingsService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
@Module
class WsSettingsAPIModule {
    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit): WsSettingsService {
        return retrofit.create(WsSettingsService::class.java)
    }
}