package com.ditto.mylibrary.data.di

import com.ditto.mylibrary.data.MyLibraryImpl
import com.ditto.mylibrary.data.MyLibraryRepositoryImpl
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.MyLibraryUseCase
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [UseCaseModule::class])
interface MyLibraryDataModule {
    @Binds
    fun bindmyLibraryRepository(myLibraryRepositoryImpl: MyLibraryRepositoryImpl): MyLibraryRepository
}

@Module
internal interface UseCaseModule {
    @Binds
    fun bindMyLibraryUseCase(
        myLibraryImpl: MyLibraryImpl
    ): MyLibraryUseCase
}