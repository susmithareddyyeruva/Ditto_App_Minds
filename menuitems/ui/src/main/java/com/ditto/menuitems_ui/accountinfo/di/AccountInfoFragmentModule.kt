package com.ditto.menuitems_ui.accountinfo.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.accountinfo.fragment.AccountInfoFragment
import com.ditto.menuitems_ui.accountinfo.fragment.AccountInfoViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface AccountInfoViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AccountInfoViewModel::class)
    fun bindAccountInfoViewModel(viewModel: AccountInfoViewModel): ViewModel

}

@Module
interface AccountInfoFragmentModule {
        @ContributesAndroidInjector(modules = [AccountInfoViewModelModule::class])
        fun AccountInfoFragment(): AccountInfoFragment
}