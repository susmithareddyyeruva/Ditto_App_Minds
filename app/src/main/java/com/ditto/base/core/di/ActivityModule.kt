package com.ditto.base.core.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import core.di.scope.PerActivity
import core.ui.BottomNavViewModel
import core.ui.BottomNavigationActivity
import core.ui.ToolbarViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ActivityModule {

    @Binds
    fun bindsBottomNavigationActivity(activity: BottomNavigationActivity): AppCompatActivity

    @Binds
    @IntoMap
    @PerActivity
    @ViewModelKey(BottomNavViewModel::class)
    fun bindBottomNavViewModel(viewModel: BottomNavViewModel): ViewModel

    @Binds
    @IntoMap
    @PerActivity
    @ViewModelKey(ToolbarViewModel::class)
    fun bindToolbarViewModel(viewModel: ToolbarViewModel): ViewModel
}