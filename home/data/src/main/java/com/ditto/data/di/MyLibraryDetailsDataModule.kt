package com.ditto.data.di

import com.ditto.data.GetHomeDataUseCaseImpl
import com.ditto.data.MyLibraryRepositoryImpl
import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.HomeUsecase
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [MyLibraryUseCaseModule::class])
interface MyLibraryDetailsDataModule {
    @Binds
    fun bindMyLibraryRepository(settingsImpl: MyLibraryRepositoryImpl): GetMyLibraryRepository
}

@Module
internal interface MyLibraryUseCaseModule {
    @Binds
    fun bindMyLibraryUsecase(
        getHomeDataUseCaseImpl: GetHomeDataUseCaseImpl
    ): HomeUsecase
}