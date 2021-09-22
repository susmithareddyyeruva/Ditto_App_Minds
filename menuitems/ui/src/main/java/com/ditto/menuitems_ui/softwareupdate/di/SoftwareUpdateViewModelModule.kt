package com.ditto.menuitems_ui.softwareupdate.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.softwareupdate.SoftwareUpdateFragment
import com.ditto.menuitems_ui.softwareupdate.SoftwareUpdateViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module
interface SoftwareUpdateViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SoftwareUpdateViewModel::class)
    fun bindAboutAppViewModel(viewModel: SoftwareUpdateViewModel): ViewModel
}

@Module
interface SoftwareUpdateFragmentModule {
    @ContributesAndroidInjector(modules = [SoftwareUpdateViewModelModule::class])
    fun SoftwareUpdateFragment(): SoftwareUpdateFragment
}