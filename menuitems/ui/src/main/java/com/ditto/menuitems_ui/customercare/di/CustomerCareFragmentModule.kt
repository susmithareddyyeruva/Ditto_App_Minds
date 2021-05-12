package com.ditto.menuitems_ui.customercare.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.customercare.fragment.CustomerCareFragment
import com.ditto.menuitems_ui.customercare.fragment.CustomerCareViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface CustomerCareViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CustomerCareViewModel::class)
    fun bindCustomercareViewModel(viewModel: CustomerCareViewModel): ViewModel

}

@Module
interface CustomerCareFragmentModule {
        @ContributesAndroidInjector(modules = [CustomerCareViewModelModule::class])
        fun CustomerCareFragment(): CustomerCareFragment
}