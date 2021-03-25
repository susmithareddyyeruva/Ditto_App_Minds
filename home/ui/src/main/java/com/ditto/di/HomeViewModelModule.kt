package com.ditto.di

import androidx.lifecycle.ViewModel
import com.ditto.home_ui.*
import com.example.home_ui.*
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


/**
 * Dagger module to provide LoginViewModel functionality.
 */
@Module
interface HomeViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BuyPatternViewModel::class)
    fun bindBuyPatternViewModel(viewModel: BuyPatternViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShopViewModel::class)
    fun bindShopViewModel(viewModel: ShopViewModel): ViewModel
}

@Module
interface HomeFragmentModule {
    @ContributesAndroidInjector(modules = [HomeViewModelModule::class])
    fun homeFragment(): HomeFragment

    @ContributesAndroidInjector(modules = [HomeViewModelModule::class])
    fun buyPatternFragment(): BuyPatternFragment

    @ContributesAndroidInjector(modules = [HomeViewModelModule::class])
    fun shopFragment(): ShopFragment
}