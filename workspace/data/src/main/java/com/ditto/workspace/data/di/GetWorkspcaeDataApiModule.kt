package com.ditto.workspace.data.di

import com.ditto.workspace.data.api.GetWorkspaceService
import core.di.scope.WbApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit


@Module
class GetWorkspcaeDataApiModule {
    @Provides
    @Reusable
    fun provideRetrofitService(@WbApiRetrofit retrofit: Retrofit): GetWorkspaceService {
        return retrofit.create(GetWorkspaceService::class.java)
    }
}
