package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.api.FAQGlossaryService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class FAQGlossaryApiModule {
    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit): FAQGlossaryService {
        return retrofit.create(FAQGlossaryService::class.java)
    }
}