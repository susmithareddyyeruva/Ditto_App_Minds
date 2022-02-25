package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.api.AccountInfoService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import retrofit2.create

@Module
class AccountInfoAPIModule {

    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit) :AccountInfoService{
        return retrofit.create(AccountInfoService::class.java)
    }
}