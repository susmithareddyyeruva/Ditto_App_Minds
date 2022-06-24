package com.ditto.menuitems_ui.subscription_info.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.subscription_info.fragment.SubscriptionInfoFragment
import com.ditto.menuitems_ui.subscription_info.fragment.SubscriptionInfoViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface SubscriptionInfoViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SubscriptionInfoViewModel::class)
    fun SubscriptionInfoViewModel(viewModel: SubscriptionInfoViewModel): ViewModel
}

@Module
interface SubscriptionInfoFragmentModule {
    @ContributesAndroidInjector(modules = [SubscriptionInfoViewModelModule::class])
    fun SubscriptionInfoFragment(): SubscriptionInfoFragment
}