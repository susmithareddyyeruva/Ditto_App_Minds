package com.ditto.menuitems_ui.privacyandsettings.ui

import android.util.Log
import androidx.databinding.ObservableField
import com.ditto.menuitems.domain.AboutAppUseCase
import com.ditto.menuitems.domain.model.AboutAppDomain
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import javax.inject.Inject

class PrivacyAndSettingsViewModel @Inject constructor(private val aboutAppUseCase: AboutAppUseCase) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var data: ObservableField<String> = ObservableField("")

    fun fetchUserData() {
        disposable += aboutAppUseCase.getAboutAppAndPrivacyData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{ handleFetchResult(it) }
    }


    private fun handleFetchResult(result: Result<AboutAppDomain>?) {
        when(result)
        {
            is Result.OnSuccess<AboutAppDomain> ->{
                Log.d("PrivacyPolicy", "Success"+result.data)
               data.set(result.data.c_body)
                uiEvents.post(Event.updateResponseinText)

            }
            is Result.OnError -> {
                Log.d("PrivacyPolicy", "Failed")
            }
        }
    }


    sealed class Event {
        object updateResponseinText : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
    }

}