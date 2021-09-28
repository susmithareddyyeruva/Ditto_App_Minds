package core.ui

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import core.data.model.SoftwareUpdateResult
import core.domain.SoftwareupdateUseCase
import core.ui.rxbus.RxBus
import core.ui.rxbus.RxBusEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
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
               RxBus.publish(RxBusEvent.versionReceived(result.data))
            }
            is Result.OnError -> {
              RxBus.publish(RxBusEvent.versionErrorReceived("Something Went Worng!!"))
            }
        }
    }

}