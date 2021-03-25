package com.ditto.onboarding.ui.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import com.ditto.onboarding.ui.OnboardingFragment
import com.ditto.onboarding.ui.OnboardingViewModel
import core.ui.ViewModelKey

/**
 * Dagger module to provide LoginViewModel functionality.
 */
@Module
interface  OnBoardingViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    fun bindOnBoardingViewModel(viewModel: OnboardingViewModel): ViewModel
}

@Module
interface OnBoardingFragmentModule {
    @ContributesAndroidInjector(modules = [ OnBoardingViewModelModule::class])
    fun onBoardingFragment(): OnboardingFragment
}