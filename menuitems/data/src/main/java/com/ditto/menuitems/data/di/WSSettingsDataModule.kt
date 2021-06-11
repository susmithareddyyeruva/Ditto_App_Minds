package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.GetWorkspaceProImpl
import com.ditto.menuitems.data.WorkspaceProRepositoryImpl
import com.ditto.menuitems.domain.GetWorkspaceProData
import com.ditto.menuitems.domain.WorkspaceProRepository
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [UseCaseModule::class])
interface WSSettingsDataModule {
    @Binds
    fun bindSettingsRepository(settingsImpl: WorkspaceProRepositoryImpl): WorkspaceProRepository
}

@Module
internal interface UseCaseModule {
    @Binds
    fun bindWSProUsecase(
        getDbUseCaseImpl: GetWorkspaceProImpl
    ): GetWorkspaceProData

}