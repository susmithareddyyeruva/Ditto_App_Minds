package com.ditto.menuitems_ui.settings.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.settings.WSProSettingViewModel
import com.ditto.menuitems_ui.settings.WSProSettingsFragment
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface WSProSettingViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(WSProSettingViewModel::class)
    fun bindWsProSettingViewModel(viewModel: WSProSettingViewModel): ViewModel

}

@Module
interface WSProSettingsModule {
    @ContributesAndroidInjector(modules = [WSProSettingViewModelModule::class])
    fun WSProSettingsFragment(): WSProSettingsFragment
}