package com.ditto.menuitems_ui.managedevices.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.managedevices.fragment.ManageDeviceFragment
import com.ditto.menuitems_ui.managedevices.fragment.ManageDeviceViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface ManageDeviceViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ManageDeviceViewModel::class)
    fun bindManageDeviceViewModel(viewModel: ManageDeviceViewModel): ViewModel
}

@Module
interface ManageDeviceFragmentModule {
    @ContributesAndroidInjector(modules = [ManageDeviceViewModelModule::class])
    fun ManageDeviceFragment(): ManageDeviceFragment
}