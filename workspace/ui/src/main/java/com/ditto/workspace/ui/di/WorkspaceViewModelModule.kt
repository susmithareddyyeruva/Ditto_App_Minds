package com.ditto.workspace.ui.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import com.ditto.workspace.ui.PatternInstructionsFragment
import com.ditto.workspace.ui.WorkspaceFragment
import com.ditto.workspace.ui.WorkspaceTabFragment
import com.ditto.workspace.ui.WorkspaceViewModel
import core.ui.ViewModelKey

/**
 * Dagger module to provide LoginViewModel functionality.
 */
@Module
interface  WorkspaceViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(WorkspaceViewModel::class)
    fun bindWorkspaceViewModel(viewModel: WorkspaceViewModel): ViewModel
}

@Module
interface WorkspaceFragmentModule {
    @ContributesAndroidInjector(modules = [ WorkspaceViewModelModule::class])
    fun workspaceFragment(): WorkspaceFragment

    @ContributesAndroidInjector(modules = [ WorkspaceViewModelModule::class])
    fun workspaceTabFragment(): WorkspaceTabFragment

    @ContributesAndroidInjector(modules = [ WorkspaceViewModelModule::class])
    fun patternInstructionsFragment(): PatternInstructionsFragment
}