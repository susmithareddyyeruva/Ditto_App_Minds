package com.ditto.menuitems_ui.aboutapp.fragment

import android.util.Log
import com.ditto.menuitems.domain.AbstractForAboutAppViewModel
import com.ditto.menuitems.domain.model.AboutAppDomain
import com.ditto.menuitems.domain.model.AboutAppResponseData
import com.ditto.menuitems_ui.settings.WSProSettingViewModel
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import non_core.lib.Result


class AboutAppViewModel @Inject constructor(private val aboutAppAbstract:AbstractForAboutAppViewModel): BaseViewModel() {

    var txt:String=""
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    fun fetchUserData() {
        disposable += aboutAppAbstract.getAboutAppAndPrivacyData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{ handleFetchResult(it) }
    }


    private fun handleFetchResult(result: Result<AboutAppDomain>?) {
        when(result)
        {
            is Result.OnSuccess<AboutAppDomain> ->{
                uiEvents.post(Event.OnHideProgress)

                Log.d("AboutAppViewModel", "Success"+result.data)
                setResponseText(result.data.c_body)
                uiEvents.post(Event.updateResponseinText)

            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)

                Log.d("WSProSettingViewModel", "Failed")
            }
        }
    }

    fun setResponseText(txt: String){
        this.txt=txt
    }

    fun getResponseText():String{
        return txt;
    }



    sealed class Event {
        object updateResponseinText : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
    }


}