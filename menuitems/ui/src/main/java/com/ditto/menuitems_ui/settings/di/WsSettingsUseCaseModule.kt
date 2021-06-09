package com.ditto.menuitems_ui.settings.di

import com.ditto.menuitems_ui.settings.UseCases
import com.ditto.menuitems_ui.settings.WsSettingsRepositoryImpl
import com.ditto.menuitems_ui.settings.domain.GetWsSettingUseCaseImpl
import com.ditto.menuitems_ui.settings.domain.SettingsRepository
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [UseCaseModule::class])
interface WsSettingsUseCaseModule {
    @Binds
    fun bindSettingsRepository(settingsImpl: WsSettingsRepositoryImpl): SettingsRepository
}

@Module
internal interface UseCaseModule {
    @Binds
    fun bindGetLoginDbUseCase(
        getDbUseCaseImpl: GetWsSettingUseCaseImpl
    ): UseCases

}