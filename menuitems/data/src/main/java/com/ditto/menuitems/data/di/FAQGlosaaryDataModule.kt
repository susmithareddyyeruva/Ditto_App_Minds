package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.FAQGlossaryRepositoryImpl
import com.ditto.menuitems.data.GetFAQGlosaaryUsecaseImpl
import com.ditto.menuitems.domain.FAQGlossaryRepository
import com.ditto.menuitems.domain.FAQGlossaryUseCase
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [FAQGlossaryUseCaseModule::class])
interface FAQGlosaaryDataModule {
    @Binds
    fun bindFAQGlossaryRepository(faqGlossaryRepository: FAQGlossaryRepositoryImpl): FAQGlossaryRepository
}

@Module
internal interface FAQGlossaryUseCaseModule {
    @Binds
    fun bindFAQGlossaryUsecase(
        faqGlosaaryUsecaseImpl: GetFAQGlosaaryUsecaseImpl
    ): FAQGlossaryUseCase

}