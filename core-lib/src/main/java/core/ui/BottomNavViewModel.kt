package core.ui

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import core.OCAPI_PASSWORD
import core.OCAPI_USERNAME
import core.appstate.AppState
import core.data.TokenRequest
import core.data.TokenResultDomain
import core.domain.GetTokenUseCase
import core.event.UiEvents
import core.lib.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import java.util.*
import javax.inject.Inject

class BottomNavViewModel @Inject constructor() : BaseViewModel() {

    val visibility = ObservableBoolean(true)
    val showProgress = ObservableBoolean(false)
    val menuTitle: ObservableField<String> = ObservableField("")
    val menuDescription: ObservableField<String> = ObservableField("")

    val isGuestBase: ObservableBoolean = ObservableBoolean(false)
    var userEmailBase: ObservableField<String> = ObservableField("")
    var userPhoneBase: ObservableField<String> = ObservableField("")
    var userFirstNameBase: ObservableField<String> = ObservableField("")
    var userLastNameBase: ObservableField<String> = ObservableField("")
    val isLogoutEvent: ObservableBoolean = ObservableBoolean(false)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    init {
        if (AppState.getIsLogged())
            isGuestBase.set(false) else
            isGuestBase.set(true)
    }

    fun refreshMenu(context: Context?) {
        if (!AppState.getIsLogged()) {
            menuTitle.set(context?.getString(R.string.hi_there))
            menuDescription.set(context?.getString(R.string.sign_in_to_explore_more))

        } else {
            menuTitle.set(userFirstNameBase.get() + userLastNameBase.get())
            menuDescription.set(userEmailBase.get())
        }
    }

    fun logout() {
        uiEvents.post(Event.NavigateToLogin)
        isLogoutEvent.set(true)
    }
    fun sigin() {
        uiEvents.post(Event.NavigateToLogin)
    }
    sealed class Event {
        object NavigateToLogin : Event()
    }


}
