package com.ditto.menuitems_ui.faq.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ditto.menuitems.domain.FAQGlossaryUseCase
import com.ditto.menuitems.domain.model.faq.FAQGlossaryResultDomain
import com.ditto.menuitems.domain.model.faq.FaqGlossaryResponseDomain
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import javax.inject.Inject

class FAQGlossaryfragmentViewModel @Inject constructor(val context: Context,
val useCase: FAQGlossaryUseCase) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    var data: MutableLiveData<FaqGlossaryResponseDomain> = MutableLiveData()
    val events = uiEvents.stream()

    fun fetchData() {
        disposable += useCase.getFAQGlossaryDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }


    }

    /**
     * Handling fetch result here.....
     */
    private fun handleFetchResult(result: Result<FAQGlossaryResultDomain>?) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess-> {
                uiEvents.post(Event.OnResultSuccess)
                data.value =result.data.c_body
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
                Log.d("faq_glossary", "Failed")
            }
        }
    }

    sealed class Event {
        object OnResultSuccess : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
    }
}

