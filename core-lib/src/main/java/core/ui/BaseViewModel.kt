package core.ui

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
    var disposable: CompositeDisposable = CompositeDisposable()
    val activeInternetConnection: ObservableBoolean = ObservableBoolean(true)
    val activeSocketConnection: ObservableBoolean = ObservableBoolean(false)
    val isSetUpError: ObservableBoolean = ObservableBoolean(false)
    val isCalibrated: ObservableBoolean = ObservableBoolean(false)
//    val isUserNeedCalibrated: ObservableBoolean = ObservableBoolean(false)
    val isSaveExitButtonClicked: ObservableBoolean = ObservableBoolean(false)
    val isProjecting: ObservableBoolean = ObservableBoolean(false)

    val isGuest: ObservableBoolean = ObservableBoolean(false)
    var userEmail: String? = ""
    var userPhone: String? = ""
    var userFirstName: String? = ""
    var userLastName: String? = ""
    var subscriptionEndDate: String? = ""

}