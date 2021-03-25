package core.di

import android.content.Context
import base.TraceApplication
import com.ditto.calibration_ui.di.CalibrationFragmentModule
import com.ditto.data.di.LoginDbModule
import com.ditto.data.di.SplashDbModule
import com.ditto.howto_ui.di.HowtoFragmentModule
import com.ditto.logger.di.LoggerModule
import com.ditto.example.tutorial_ui.di.TutorialFragmentModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import com.ditto.di.HomeFragmentModule
import com.ditto.di.LoginFragmentModule
import com.ditto.storage.data.di.StorageModule
import com.ditto.storage.data.di.TraceDbModule
import com.ditto.instruction.ui.di.InstructionFragmentModule
import com.ditto.instructions.data.di.InstructionModule
import com.ditto.mylibrary.data.di.MyLibraryDataModule
import com.ditto.mylibrary.ui.di.MyLibraryFragmentModule
import com.ditto.onboarding.data.di.OnboardingDataModule
import com.ditto.onboarding.ui.di.OnBoardingFragmentModule
import com.ditto.ui.SplashFragmentModule
import com.ditto.workspace.data.di.WorkspaceDataModule
import com.ditto.workspace.ui.di.WorkspaceFragmentModule
import com.ditto.howto_data.di.HowToModule
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
interface AppComponent : AndroidInjector<TraceApplication> {

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun setContext(context: Context): Builder
    }
}