package com.ditto.mylibrary.data.di

import com.ditto.mylibrary.data.api.MyLibraryFilterService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class MyLibraryFilterModule {

    @Provides
    @Reusable
    fun provideMyLibraryRetrofitService(@WbApiRetrofit retrofit: Retrofit): MyLibraryFilterService {
        return retrofit.create(MyLibraryFilterService::class.java)
    }
}