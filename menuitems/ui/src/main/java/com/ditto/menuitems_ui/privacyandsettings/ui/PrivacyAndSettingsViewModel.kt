package com.ditto.menuitems_ui.privacyandsettings.ui

import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.domain.AboutAppUseCase
import com.ditto.menuitems.domain.model.AboutAppDomain
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

class PrivacyAndSettingsViewModel @Inject constructor(private val aboutAppUseCase: AboutAppUseCase) :
    BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var data: String = ""

    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(PrivacyAndSettingsViewModel::class.java.simpleName)
    }

    fun fetchUserData() {
        uiEvents.post(Event.OnShowProgress)
        disposable += aboutAppUseCase.getAboutAppAndPrivacyData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }


    private fun handleFetchResult(result: Result<AboutAppDomain>?) {
        when (result) {
            is Result.OnSuccess<AboutAppDomain> -> {
                logger.d("PrivacyPolicy, Success" + result.data)
                data = result.data.cBody
                uiEvents.post(Event.OnResultSuccess)

            }
            is NoNetworkError -> {
                uiEvents.post(Event.OnHideProgress)
                uiEvents.post(Event.NoNetworkError)
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
                logger.d("PrivacyPolicy, Failed")
            }
        }
    }


    sealed class Event {
        object OnResultSuccess : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object NoNetworkError : Event()
    }

}