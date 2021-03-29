package com.ditto.login.data.di

import com.ditto.login.data.LoginService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
@Module
class LoginApiModule {
    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }
}