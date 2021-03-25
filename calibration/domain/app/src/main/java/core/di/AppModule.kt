package core.di

import androidx.lifecycle.ViewModelProvider
import core.ui.BottomNavigationActivity
import core.di.scope.PerActivity
import core.ui.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AppModule {
    @ContributesAndroidInjector(modules = [ActivityModule::class])

    @PerActivity
    fun traceActivityInjector(): BottomNavigationActivity

    @Binds
    fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}