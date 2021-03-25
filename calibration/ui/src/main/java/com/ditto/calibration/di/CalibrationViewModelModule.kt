package com.ditto.calibration.di
/**
 * Created by Vishnu A V on  10/08/2020.
 * Dagger module to provide CalibrationViewModel functionality.
 */
import androidx.lifecycle.ViewModel
import com.ditto.calibration.ui.CalibrationFragment
import com.ditto.calibration.ui.CalibrationViewModel
import core.ui.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface CalibrationViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CalibrationViewModel::class)
    fun bindCalibrationViewModel(viewModel: CalibrationViewModel):ViewModel

}

@Module
interface CalibrationFragmentModule {
    @ContributesAndroidInjector(modules = [CalibrationViewModelModule :: class])
    fun calibrationfragment() : CalibrationFragment
}