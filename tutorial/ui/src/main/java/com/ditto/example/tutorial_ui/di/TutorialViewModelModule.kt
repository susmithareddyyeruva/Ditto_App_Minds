package com.ditto.example.tutorial_ui.di

import androidx.lifecycle.ViewModel
import com.ditto.example.tutorial_ui.TutorialFragment
import com.ditto.example.tutorial_ui.TutorialViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface TutorialViewModelModule  {
    @Binds
    @IntoMap
    @ViewModelKey(TutorialViewModel::class)
    fun bindTutorialViewModel(viewModel: TutorialViewModel): ViewModel

}

@Module
interface TutorialFragmentModule {
    @ContributesAndroidInjector(modules = [TutorialViewModelModule:: class])
    fun tutorialfragment() : TutorialFragment
}