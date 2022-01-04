package com.ditto.menuitems_ui.faq.ui

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
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
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

class FAQGlossaryFragmentViewModel @Inject constructor(
    val context: Context,
    private val useCase: FAQGlossaryUseCase
) : BaseViewModel() {
    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(FAQGlossaryFragmentViewModel::class.java.simpleName)
    }
    private val uiEvents = UiEvents<Event>()
    var data: MutableLiveData<FaqGlossaryResponseDomain> = MutableLiveData()
    val events = uiEvents.stream()
    var errorString: ObservableField<String> = ObservableField("")

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
            is Result.OnSuccess -> {
                uiEvents.post(Event.OnHideProgress)
                uiEvents.post(Event.OnResultSuccess)
                data.value = result.data.c_body
            }
            is Result.OnError -> {
                uiEvents.post(Event.OnHideProgress)
                logger.d("faq_glossary, Failed")
                handleError(result.error)
            }
        }
    }

    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                errorString.set(error.message)
                uiEvents.post(Event.NoInternet)
            }
            else -> {
                errorString.set(error.message)
                uiEvents.post(Event.OnResultFailed)
            }

        }
    }

    sealed class Event {
        object OnResultSuccess : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object OnResultFailed : Event()
        object NoInternet : Event()
    }
}

