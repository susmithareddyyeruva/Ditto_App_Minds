package com.ditto.menuitems.data.di


import com.ditto.menuitems.data.AboutAppAbstractImpl
import com.ditto.menuitems.data.AboutAppRepositoryImpl
import com.ditto.menuitems.domain.AboutAppAbstractRespository
import com.ditto.menuitems.domain.AbstractForAboutAppViewModel
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [AbstractModule::class])
interface AboutAppDataModule {
    @Binds
    fun bindSettingsRepository(settingsImpl: AboutAppRepositoryImpl): AboutAppAbstractRespository
}

@Module
internal interface AbstractModule {
    @Binds
    fun bindWSProUsecase(
        getDbUseCaseImpl: AboutAppAbstractImpl
    ): AbstractForAboutAppViewModel

}