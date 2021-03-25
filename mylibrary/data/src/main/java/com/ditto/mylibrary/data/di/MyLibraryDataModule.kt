package com.ditto.mylibrary.data.di

import dagger.Binds
import dagger.Module
import com.ditto.mylibrary.data.MyLibraryRepositoryImpl
import com.ditto.mylibrary.data.MyLibraryImpl
import trace.mylibrary.domain.GetMylibraryData
import trace.mylibrary.domain.MyLibraryRepository

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
    ): GetMylibraryData
}