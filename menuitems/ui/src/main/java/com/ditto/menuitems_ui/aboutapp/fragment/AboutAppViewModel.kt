package com.ditto.menuitems_ui.aboutapp.fragment

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
import javax.inject.Inject


class AboutAppViewModel @Inject constructor(private val aboutAppUseCase: AboutAppUseCase): BaseViewModel() {

    var txt:String=""
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(AboutAppViewModel::class.java.simpleName)
    }
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
               logger.d("AboutAppViewMode Success"+result.data)
                setResponseText(result.data.cBody)
                uiEvents.post(Event.UpdateResponseText)

            }
            is Result.OnError -> {
                logger.d("WSProSettingViewModel Failed")
            }
            else -> {}
        }
    }

    fun setResponseText(txt: String){
        this.txt=txt
    }

    fun getResponseText():String{
        return txt;
    }



    sealed class Event {
        object UpdateResponseText : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
    }


}