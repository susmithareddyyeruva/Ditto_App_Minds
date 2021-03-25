package com.ditto.base.core.di

import android.content.Context
import com.ditto.calibration.di.CalibrationFragmentModule
import com.ditto.login.data.di.LoginDbModule
import com.ditto.splash.data.di.SplashDbModule
import com.ditto.home.di.HomeFragmentModule
import com.ditto.login.di.LoginFragmentModule
import com.ditto.example.tutorial_ui.di.TutorialFragmentModule
import com.ditto.howto.di.HowtoFragmentModule
import com.ditto.instructions.ui.di.InstructionFragmentModule
import com.ditto.instructions.data.di.InstructionModule
import com.ditto.logger.di.LoggerModule
import com.ditto.mylibrary.data.di.MyLibraryDataModule
import com.ditto.mylibrary.ui.di.MyLibraryFragmentModule
import com.ditto.onboarding.data.di.OnboardingDataModule
import com.ditto.onboarding.ui.di.OnBoardingFragmentModule
import com.ditto.storage.data.di.StorageModule
import com.ditto.storage.data.di.TraceDbModule
import com.ditto.splash.ui.SplashFragmentModule
import com.ditto.workspace.data.di.WorkspaceDataModule
import com.ditto.workspace.ui.di.WorkspaceFragmentModule
import com.ditto.howto_data.model.di.HowToModule
import core.di.RetrofitModule
import com.ditto.base.DittoApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

import javax.inject.Singleton

/**
 * Dagger App component declaration
 */
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class, LoggerModule::class, LoginDbModule::class,
        SplashDbModule::class, StorageModule::class, RetrofitModule::class, TraceDbModule::class, SplashFragmentModule::class,
        LoginFragmentModule::class, InstructionFragmentModule::class, InstructionModule::class, HomeFragmentModule::class,
        OnBoardingFragmentModule::class, OnboardingDataModule::class, MyLibraryFragmentModule::class, MyLibraryDataModule::class,
        WorkspaceFragmentModule::class, WorkspaceDataModule::class, CalibrationFragmentModule::class,
        TutorialFragmentModule::class, HowtoFragmentModule::class, HowToModule::class]
)
@Singleton
interface AppComponent : AndroidInjector<DittoApplication> {

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun setContext(context: Context): Builder
    }
}