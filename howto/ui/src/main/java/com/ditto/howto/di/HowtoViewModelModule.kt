package com.ditto.howto.di

import androidx.lifecycle.ViewModel
import com.ditto.howto.ui.HowtoFragment
import com.ditto.howto.ui.HowtoViewModel
import com.ditto.howto.fragment.TabContentFragment
import com.ditto.howto.ui.HowtoInstructionsFragment
import com.ditto.howto.ui.HowtoInstructionsViewModel
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

    @Binds
    @IntoMap
    @ViewModelKey(HowtoInstructionsViewModel::class)
    fun bindHowtoInstructionsViewModel(viewModel: HowtoInstructionsViewModel): ViewModel

}

@Module
interface HowtoFragmentModule{
    @ContributesAndroidInjector(modules = [HowtoViewModelModule::class])
    fun howtofragment() : HowtoFragment

    @ContributesAndroidInjector(modules = [HowtoViewModelModule::class])
    fun tabcontentfragment() : TabContentFragment

    @ContributesAndroidInjector(modules = [HowtoViewModelModule::class])
    fun howtoInstructionsFragment() : HowtoInstructionsFragment

}