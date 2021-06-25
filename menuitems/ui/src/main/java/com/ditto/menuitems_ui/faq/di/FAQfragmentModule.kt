package com.ditto.menuitems_ui.faq.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.faq.ui.FAQFragment
import com.ditto.menuitems_ui.faq.ui.FQAfragmentViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap


@Module
interface FAQfragmentViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FQAfragmentViewModel::class)
    fun bindFAQfragmentViewModel(viewModel: FQAfragmentViewModel): ViewModel

}

@Module
interface FAQfragmentModule {
    @ContributesAndroidInjector(modules = [FAQfragmentViewModelModule::class])
    fun FQAfragment(): FAQFragment
}