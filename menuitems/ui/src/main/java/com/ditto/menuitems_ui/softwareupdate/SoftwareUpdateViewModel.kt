package com.ditto.menuitems_ui.softwareupdate

import androidx.lifecycle.MutableLiveData
import core.data.model.SoftwareUpdateResult
import core.domain.SoftwareupdateUseCase
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import javax.inject.Inject

class SoftwareUpdateViewModel @Inject constructor (
    private val versionUseCase: SoftwareupdateUseCase
) : BaseViewModel() {
    var data: MutableLiveData<SoftwareUpdateResult> = MutableLiveData()
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    init {
        checkVersion()
    }

    fun checkVersion(){

        disposable += versionUseCase.getVersion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleVersionResult(it) }
    }

    private fun handleVersionResult(result: Result<SoftwareUpdateResult>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                uiEvents.post(Event.OnVersionCheckCompleted)
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnVersionCheckCompleted)
            }
        }
    }
    sealed class Event {
        object OnVersionCheckCompleted : Event()
    }
}