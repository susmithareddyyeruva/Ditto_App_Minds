package core.di

import core.di.scope.WbTokenApiRetrofit
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
class TokenApiModule {

    @Provides
    @Reusable
    fun provideTokenRetrofitService(@WbTokenApiRetrofit retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}