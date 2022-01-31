package com.ditto.menuitems_ui.faq.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.faq.ui.FAQGlossaryFragmentViewModel
import com.ditto.menuitems_ui.faq.ui.FaqGlossaryMainFragment
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface FAQGlossaryFragmentViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FAQGlossaryFragmentViewModel::class)
    fun bindFAQGlossaryfragmentViewModel(viewModel: FAQGlossaryFragmentViewModel): ViewModel

}

@Module
interface FaqGlossaryMainFragmentModule {
    @ContributesAndroidInjector(modules = [FAQGlossaryFragmentViewModelModule::class])
    fun FaqGlossaryMainFragment(): FaqGlossaryMainFragment
}