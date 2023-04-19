package com.ditto.menuitems_ui.shareyourcraft.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.shareyourcraft.ui.ShareImageFragment
import com.ditto.menuitems_ui.shareyourcraft.ui.ShareImageViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface ShareYourCraftViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ShareImageViewModel::class)
    fun bindShareImageViewModel(viewModel: ShareImageViewModel): ViewModel

}

@Module
interface ShareYourCraftFragmentModule {
    @ContributesAndroidInjector(modules = [ShareYourCraftViewModelModule::class])
    fun ShareImageFragment(): ShareImageFragment
}