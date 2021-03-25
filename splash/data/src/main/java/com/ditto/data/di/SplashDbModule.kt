package com.ditto.data.di

import com.ditto.data.DbRepositoryImpl
import com.ditto.data.GetDbUseCaseImpl
import com.ditto.data.UpdateDbUseCaseImpl
import com.ditto.splash.domain.DbRepository
import com.ditto.splash.domain.GetDbDataUseCase
import com.ditto.splash.domain.UpdateDbUseCase
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [SplashUseCaseModule::class])
interface SplashDbModule {
    @Binds
    fun bindDbRepository(dbRepositoryImpl: DbRepositoryImpl): DbRepository
}

@Module
internal interface SplashUseCaseModule {
    @Binds
    @Singleton
    fun bindsGetDbDataUseCase(
        getDbUseCaseImpl: GetDbUseCaseImpl
    ): GetDbDataUseCase

    @Binds
    @Singleton
    fun bindsUpdateDbUseCase(
        updateDbUseCaseImpl: UpdateDbUseCaseImpl
    ): UpdateDbUseCase

}