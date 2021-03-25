package com.ditto.splash.ui

import androidx.lifecycle.ViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger module to provide SplashViewModel functionality.
 */
@Module
interface SplashViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel
}

@Module
interface SplashFragmentModule {
    @ContributesAndroidInjector(modules = [SplashViewModelModule::class])
    fun splashFragment(): SplashFragment
}
