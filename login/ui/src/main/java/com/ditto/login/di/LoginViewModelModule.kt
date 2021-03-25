package com.ditto.login.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import com.ditto.login.ui.LoginFragment
import com.ditto.login.ui.LoginViewModel
import core.ui.ViewModelKey

/**
 * Dagger module to provide LoginViewModel functionality.
 */
@Module
interface LoginViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel
}

@Module
interface LoginFragmentModule {
    @ContributesAndroidInjector(modules = [LoginViewModelModule::class])
    fun loginFragment(): LoginFragment
}
