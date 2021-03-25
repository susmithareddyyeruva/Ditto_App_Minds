package com.ditto.workspace.data.di

import dagger.Binds
import dagger.Module
import com.ditto.workspace.data.WorkspaceRepositoryImpl
import com.ditto.workspace.data.WorkspaceImpl
import com.ditto.workspace.domain.GetWorkspaceData
import com.ditto.workspace.domain.WorkspaceRepository

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [UseCaseModule::class])
interface WorkspaceDataModule {
    @Binds
    fun bindworkspaceRepository(workspaceRepositoryImpl: WorkspaceRepositoryImpl): WorkspaceRepository
}

@Module
internal interface UseCaseModule {
    @Binds
    fun bindWorkspaceUseCase(
        workspaceImpl: WorkspaceImpl
    ): GetWorkspaceData
}