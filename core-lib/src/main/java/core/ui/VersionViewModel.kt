package core.ui

import core.data.model.SoftwareUpdateResult
import core.domain.SoftwareupdateUseCase
import core.ui.rxbus.RxBus
import core.ui.rxbus.RxBusEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

class VersionViewModel @Inject constructor (
    private val versionUseCase: SoftwareupdateUseCase
) : BaseViewModel() {

    fun checkVersion(){

        disposable += versionUseCase.getVersion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleVersionResult(it) }
    }

    private fun handleVersionResult(result: Result<SoftwareUpdateResult>) {
        when (result) {
            is Result.OnSuccess -> {
               RxBus.publish(RxBusEvent.VersionReceived(result.data))
            }
            is Result.OnError -> {
                handleError(result.error)

            }
        }
    }

    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                RxBus.publish(RxBusEvent.VersionErrorReceived("No Internet connection available !"))
            }
            else -> {
                RxBus.publish(RxBusEvent.VersionErrorReceived("Something Went Worng!!"))
            }

        }
    }

}