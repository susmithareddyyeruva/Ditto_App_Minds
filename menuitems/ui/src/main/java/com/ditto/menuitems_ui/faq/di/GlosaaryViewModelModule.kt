package com.ditto.menuitems_ui.faq.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.faq.ui.GlossaryFragment
import com.ditto.menuitems_ui.faq.ui.GlossaryViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface GlosaaryViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(GlossaryViewModel::class)
    fun bindGlossaryViewmOdel(viewModel: GlossaryViewModel): ViewModel

}

@Module
interface GlossaryFragmentModule {
    @ContributesAndroidInjector(modules = [GlosaaryViewModelModule::class])
    fun GlossaryFragment(): GlossaryFragment
}