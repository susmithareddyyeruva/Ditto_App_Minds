package com.ditto.menuitems_ui.faq.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.faq.ui.VideosFragment
import com.ditto.menuitems_ui.faq.ui.VideosViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface VideosModule {
    @Binds
    @IntoMap
    @ViewModelKey(VideosViewModel::class)
    fun bindVideoViewmOdel(viewModel: VideosViewModel): ViewModel

}

@Module
interface VideosFragmentModule {
    @ContributesAndroidInjector(modules = [VideosModule::class])
    fun VideosFragment(): VideosFragment
}