package com.ditto.menuitems_ui.updateprojector.di

import androidx.lifecycle.ViewModel
import com.ditto.menuitems_ui.updateprojector.UpdateProjectorFragment
import com.ditto.menuitems_ui.updateprojector.fragment.UpdateProjectorViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

//Added by vineetha for update projector popup
@Module
interface UpdateProjectorViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UpdateProjectorViewModel::class)
    fun bindUpdateProjectorViewModel(viewModel: UpdateProjectorViewModel): ViewModel
}

@Module
interface UpdateProjectorFragmentModule {
    @ContributesAndroidInjector(modules = [UpdateProjectorViewModelModule::class])
    fun UpdateProjectorFragment(): UpdateProjectorFragment
}