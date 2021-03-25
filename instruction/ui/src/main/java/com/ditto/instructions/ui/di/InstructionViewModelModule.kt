package com.ditto.instructions.ui.di

/**
 * Created by Vishnu A V on  03/08/2020.
 * Dagger module to provide InstructionViewModel functionality.
 */
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import com.ditto.instructions.ui.BeamSetupFragment
import com.ditto.instructions.ui.InstructionFragment
import com.ditto.instructions.ui.InstructionViewModel
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


}

@Module
interface InstructionFragmentModule {
    @ContributesAndroidInjector(modules = [InstructionViewModelModule::class])
    fun instructionfragment(): InstructionFragment
    @ContributesAndroidInjector(modules = [InstructionViewModelModule::class])
    fun beamsetupfragment(): BeamSetupFragment

}