package com.ditto.mylibrary.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import non_core.lib.whileSubscribed
import trace.mylibrary.domain.GetMylibraryData
import trace.mylibrary.domain.model.MyLibraryData

import javax.inject.Inject

class PatternDescriptionViewModel @Inject constructor(private val getPattern: GetMylibraryData) :
    BaseViewModel() {
    // TODO: Implement the ViewModel
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isShowindicator: ObservableBoolean = ObservableBoolean(true)
    val clickedID: ObservableInt = ObservableInt(1)
    var data: MutableLiveData<MyLibraryData> = MutableLiveData()
    val patternName: ObservableField<String> = ObservableField("")
    val patternDescription: ObservableField<String> = ObservableField("")
    val patternStatus: ObservableField<String> = ObservableField("")

    val isFinalPage: ObservableBoolean = ObservableBoolean(false)
    val isStartingPage: ObservableBoolean = ObservableBoolean(true)


    init {

    }

    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
            }
        }
    }

    //fetch data from repo (via usecase)
    /* fun fetchOnPatternData() {
         disposable += getPattern.invoke()
             .whileSubscribed { it }
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribeBy { handleFetchResult(it) }
     }*/

    fun fetchPattern() {
        disposable += getPattern.getPattern(clickedID.get())
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<MyLibraryData>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                uiEvents.post(Event.OnDataUpdated)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    /**
     * [Function] ViewPager Previous Button Click
     */
    fun onClickWorkSpace() {
        uiEvents.post(Event.OnWorkspaceButtonClicked)
    }

    fun onClickInstructions() {
        uiEvents.post(Event.OnInstructionsButtonClicked)
    }

    /**
     * Events for this view model
     */
    sealed class Event {

        object OnWorkspaceButtonClicked : Event()

        object OnInstructionsButtonClicked : Event()

        object OnDataUpdated : Event()
    }
}