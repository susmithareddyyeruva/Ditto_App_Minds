package core.ui


import core.appstate.AppState
import core.data.TokenResultDomain
import core.domain.GetTokenUseCase
import core.event.RxBus
import core.event.RxBusEvent
import core.event.UiEvents
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import java.util.*
import javax.inject.Inject

class TokenViewModel @Inject constructor(
    private val tokenUseCase: GetTokenUseCase
)  : BaseViewModel() {
    private val uiEvents = UiEvents<BottomNavViewModel.Event>()
    val events = uiEvents.stream()
    fun calltoken() {
        disposable += tokenUseCase.getToken()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleTokenResult(it) }
    }
    private fun handleTokenResult(result: Result<TokenResultDomain>) {
        when (result) {
            is Result.OnSuccess -> {
                val expCal = Calendar.getInstance()
                expCal.add(Calendar.MINUTE, 25)
                val expirytime = expCal.time.time
                result.data.access_token?.let { AppState.saveToken(it,expirytime) }
                RxBus.publish(RxBusEvent.isTokenRefreshed(true))
            }
            is Result.OnError -> {
//                AppState.saveToken("",0)
            }
        }
    }
    sealed class Event {
        object OnTokenRefreshed : Event()
    }
}