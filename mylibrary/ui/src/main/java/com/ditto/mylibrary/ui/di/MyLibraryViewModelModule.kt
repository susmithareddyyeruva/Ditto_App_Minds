package com.ditto.mylibrary.ui.di

import androidx.lifecycle.ViewModel
import com.ditto.mylibrary.ui.*
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import com.ditto.mylibrary.ui.*

/**
 * Dagger module to provide LoginViewModel functionality.
 */
@Module
interface  MyLibraryViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MyLibraryViewModel::class)
    fun bindMyLibraryViewModel(viewModel: MyLibraryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AllPatternsViewModel::class)
    fun bindAllPatternsViewModel(viewModel: AllPatternsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PatternDescriptionViewModel::class)
    fun bindPatternDescriptionViewModel(viewModel: PatternDescriptionViewModel): ViewModel
}

@Module
interface MyLibraryFragmentModule {
    @ContributesAndroidInjector(modules = [ MyLibraryViewModelModule::class])
    fun myLibraryFragment(): MyLibraryFragment

    @ContributesAndroidInjector(modules = [ MyLibraryViewModelModule::class])
    fun activeProjeFragment(): ActiveProjectsFragment

    @ContributesAndroidInjector(modules = [ MyLibraryViewModelModule::class])
    fun compFragment(): CompletedProjectsFragment

    @ContributesAndroidInjector(modules = [ MyLibraryViewModelModule::class])
    fun allFragment(): AllPatternsFragment

    @ContributesAndroidInjector(modules = [ MyLibraryViewModelModule::class])
    fun patternDescriptionFragment(): PatternDescriptionFragment

    @ContributesAndroidInjector(modules = [ MyLibraryViewModelModule::class])
    fun patternInstructionsFragment(): PatternInstructionsFragment

}