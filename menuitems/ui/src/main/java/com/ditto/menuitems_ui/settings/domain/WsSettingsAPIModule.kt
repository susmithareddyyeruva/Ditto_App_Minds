package com.ditto.menuitems_ui.settings.domain

import com.ditto.menuitems_ui.settings.WsSettingsService
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