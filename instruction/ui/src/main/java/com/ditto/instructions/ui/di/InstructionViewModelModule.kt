package com.ditto.instructions.ui.di

/**
 * Created by Vishnu A V on  03/08/2020.
 * Dagger module to provide InstructionViewModel functionality.
 */
import androidx.lifecycle.ViewModel
import com.ditto.instructions.ui.*
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import core.ui.ViewModelKey

/**
 * Dagger module to provide InstructionViewModel functionality.
 */
@Module
interface InstructionViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(InstructionViewModel::class)
    fun bindInstructionViewModel(viewModel: InstructionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TutorialPdfInstructionViewModel::class)
    fun bindTutorialInstructionViewModel(viewModel: TutorialPdfInstructionViewModel): ViewModel


}

@Module
interface InstructionFragmentModule {
    @ContributesAndroidInjector(modules = [InstructionViewModelModule::class])
    fun instructionfragment(): InstructionFragment
    @ContributesAndroidInjector(modules = [InstructionViewModelModule::class])
    fun beamsetupfragment(): BeamSetupFragment
    @ContributesAndroidInjector(modules = [InstructionViewModelModule::class])
    fun tutorialPdfFragment(): TutorialPdfFragment
}