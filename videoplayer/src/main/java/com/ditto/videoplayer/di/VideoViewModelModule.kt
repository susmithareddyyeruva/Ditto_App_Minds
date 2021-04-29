package com.ditto.videoplayer.di

import androidx.lifecycle.ViewModel
import com.ditto.videoplayer.VideoPlayerFragment
import com.ditto.videoplayer.VideoPlayerViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface VideoViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(VideoPlayerViewModel::class)
    fun bindTutorialViewModel(viewModel: VideoPlayerViewModel): ViewModel

}

@Module
interface VideoPlayerFragmentModule {
    @ContributesAndroidInjector(modules = [VideoViewModelModule::class])
    fun videoPlayerFragment(): VideoPlayerFragment
}