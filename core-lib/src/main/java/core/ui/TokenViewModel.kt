package core.ui


import core.appstate.AppState
import core.data.TokenResultDomain
import core.domain.GetTokenUseCase
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
                expCal.add(Calendar.MINUTE, result.data.expires_in?:0)
                val expirytime = expCal.time.time
                result.data.access_token?.let { AppState.saveToken(it,expirytime) }
            }
            is Result.OnError -> {
                AppState.saveToken("",0)
            }
        }
    }
}