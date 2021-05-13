
package com.ditto.base.core.di

import android.content.Context
import com.ditto.base.DittoApplication
import com.ditto.calibration.di.CalibrationFragmentModule
import com.ditto.home.di.HomeFragmentModule
import com.ditto.howto.di.HowToModule
import com.ditto.howto.di.HowtoFragmentModule
import com.ditto.instructions.data.di.InstructionModule
import com.ditto.instructions.ui.di.InstructionFragmentModule
import com.ditto.logger.di.LoggerModule
import com.ditto.login.data.di.LoginApiModule
import com.ditto.login.data.di.LoginDbModule
import com.ditto.login.di.LoginFragmentModule
import com.ditto.menuitems_ui.customercare.di.CustomerCareFragmentModule
import com.ditto.menuitems_ui.faq.di.FAQfragmentModule
import com.ditto.mylibrary.data.di.MyLibraryDataModule
import com.ditto.mylibrary.ui.di.MyLibraryFragmentModule
import com.ditto.onboarding.data.di.OnboardingApiModule
import com.ditto.onboarding.data.di.OnboardingDataModule
import com.ditto.onboarding.ui.di.OnBoardingFragmentModule
import com.ditto.splash.data.di.SplashDbModule
import com.ditto.splash.ui.SplashFragmentModule
import com.ditto.storage.data.di.StorageModule
import com.ditto.storage.data.di.TraceDbModule
import com.ditto.tutorial.ui.di.TutorialFragmentModule
import com.ditto.videoplayer.di.VideoPlayerFragmentModule
import com.ditto.workspace.data.di.WorkspaceDataModule
import com.ditto.workspace.ui.di.WorkspaceFragmentModule
import core.di.RetrofitModule
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
        WorkspaceFragmentModule::class, WorkspaceDataModule::class, CalibrationFragmentModule::class,LoginApiModule::class,
        TutorialFragmentModule::class, HowtoFragmentModule::class, HowToModule::class,VideoPlayerFragmentModule::class,OnboardingApiModule::class,
        CustomerCareFragmentModule::class,FAQfragmentModule::class]
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