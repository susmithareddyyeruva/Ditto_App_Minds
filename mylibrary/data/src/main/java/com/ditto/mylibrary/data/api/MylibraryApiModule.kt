package com.ditto.mylibrary.data.api

import core.di.scope.WbTailornovaApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class MylibraryApiModule {

    @Provides
    @Reusable
    fun provideTailornovaRetrofitService(@WbTailornovaApiRetrofit retrofit: Retrofit): TailornovaApiService {
        return retrofit.create(TailornovaApiService::class.java)
    }
}