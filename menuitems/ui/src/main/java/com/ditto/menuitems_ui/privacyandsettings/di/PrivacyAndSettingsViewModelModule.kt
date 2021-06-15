package com.ditto.menuitems_ui.privacyandsettings.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.privacyandsettings.ui.PrivacyAndSettingFragment
import com.ditto.menuitems_ui.privacyandsettings.ui.PrivacyAndSettingsViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface PrivacyAndSettingsViewModelModule{
    @Binds
    @IntoMap
    @ViewModelKey(PrivacyAndSettingsViewModel::class)
    fun bindPrivacyAndSettingsViewModel(viewModel: PrivacyAndSettingsViewModel):ViewModel
}

@Module
interface PrivacyAndSettingFragmentModule{
    @ContributesAndroidInjector(modules = [PrivacyAndSettingsViewModelModule::class])
    fun PrivacyAndSettingFragment() : PrivacyAndSettingFragment
}