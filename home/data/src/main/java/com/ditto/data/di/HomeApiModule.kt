package com.ditto.data.di

import com.ditto.data.api.HomeApiService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class HomeApiModule {

    @Provides
    @Reusable
    fun provideMyLibraryRetrofitService(@WbApiRetrofit retrofit: Retrofit): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }
}
