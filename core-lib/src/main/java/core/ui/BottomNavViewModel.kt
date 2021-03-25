package core.ui

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class BottomNavViewModel @Inject constructor() : BaseViewModel() {
    val visibility = ObservableBoolean(true)
}