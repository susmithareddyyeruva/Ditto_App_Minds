package com.ditto.menuitems_ui.faq.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.faq.ui.FAQGlossaryfragmentViewModel
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
    @ViewModelKey(FAQGlossaryfragmentViewModel::class)
    fun bindFAQGlossaryfragmentViewModel(viewModel: FAQGlossaryfragmentViewModel): ViewModel

}

@Module
interface FaqGlossaryMainFragmentModule {
    @ContributesAndroidInjector(modules = [FAQGlossaryFragmentViewModelModule::class])
    fun FaqGlossaryMainFragment(): FaqGlossaryMainFragment
}