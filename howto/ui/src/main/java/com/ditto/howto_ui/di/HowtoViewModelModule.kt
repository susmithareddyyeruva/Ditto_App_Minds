package com.ditto.howto_ui.di

import androidx.lifecycle.ViewModel
import com.ditto.howto_ui.HowtoFragment
import com.ditto.howto_ui.HowtoViewModel
import com.ditto.howto_ui.fragment.TabContentFragment
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by Sesha on  15/08/2020.
 * Dagger module to provide HowtoViewModel functionality.
 */
@Module
interface HowtoViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HowtoViewModel::class)
    fun bindHowtoViewModel(viewModel: HowtoViewModel): ViewModel

}

@Module
interface HowtoFragmentModule{
    @ContributesAndroidInjector(modules = [HowtoViewModelModule::class])
    fun howtofragment() : HowtoFragment

    @ContributesAndroidInjector(modules = [HowtoViewModelModule::class])
    fun tabcontentfragment() : TabContentFragment

}