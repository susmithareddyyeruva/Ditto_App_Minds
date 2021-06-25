package com.ditto.menuitems.data.di


import com.ditto.menuitems.data.AboutAppRepositoryImpl
import com.ditto.menuitems.data.AboutAppsUseCaseImpl
import com.ditto.menuitems.domain.AboutAppRepository
import com.ditto.menuitems.domain.AboutAppUseCase
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [AbstractModule::class])
interface AboutAppDataModule {
    @Binds
    fun bindSettingsRepository(settingsImpl: AboutAppRepositoryImpl): AboutAppRepository
}

@Module
internal interface AbstractModule {
    @Binds
    fun bindWSProUsecase(
        getDbUseCaseImpl: AboutAppsUseCaseImpl
    ): AboutAppUseCase

}