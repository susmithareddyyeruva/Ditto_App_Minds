package com.ditto.data.di

import com.ditto.data.api.MyLibraryService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class MyLibraryApiModule {

    @Provides
    @Reusable
    fun provideMyLibraryRetrofitService(@WbApiRetrofit retrofit: Retrofit): MyLibraryService {
        return retrofit.create(MyLibraryService::class.java)
    }
}