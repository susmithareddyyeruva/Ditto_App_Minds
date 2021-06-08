package com.ditto.menuitems_ui.aboutapp.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.aboutapp.fragment.AboutAppFragment
import com.ditto.menuitems_ui.aboutapp.fragment.AboutAppViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface AboutAppViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AboutAppViewModel::class)
    fun bindAboutAppViewModel(viewModel: AboutAppViewModel): ViewModel
}

@Module
interface AboutAppFragmentModule {
    @ContributesAndroidInjector(modules = [AboutAppViewModelModule::class])
    fun AboutAppFragment(): AboutAppFragment
}