package core.ui

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
    var disposable: CompositeDisposable = CompositeDisposable()
    val activeInternetConnection: ObservableBoolean = ObservableBoolean(true)
    val activeSocketConnection: ObservableBoolean = ObservableBoolean(false)
    val isSaveExitButtonClicked: ObservableBoolean = ObservableBoolean(false)
    val isProjecting: ObservableBoolean = ObservableBoolean(false)
}