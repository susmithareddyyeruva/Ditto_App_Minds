package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.api.AboutAppService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit


@Module
class AboutAppAPIModule {
    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit): AboutAppService {
        return retrofit.create(AboutAppService::class.java)
    }
}