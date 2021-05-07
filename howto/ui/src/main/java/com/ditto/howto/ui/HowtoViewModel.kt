package com.ditto.howto.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.howto.GetHowToDataUsecase
import com.ditto.howto.model.HowToData
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.whileSubscribed
import javax.inject.Inject

/**
 * Created by Sesha on  15/08/2020.
 * Viewmodel class representing How To Screen values
 */
class HowtoViewModel @Inject constructor(
    private val getHowTodata: GetHowToDataUsecase
) : BaseViewModel() {

    var data: MutableLiveData<HowToData> = MutableLiveData()
    val isShowindicator: ObservableBoolean = ObservableBoolean(true)
    val isErrorString: ObservableField<String> = ObservableField("")
    val isShowError: ObservableBoolean = ObservableBoolean(false)
    val isWatchVideoClicked: ObservableBoolean = ObservableBoolean(false)
    val isShowPlaceholder: ObservableBoolean = ObservableBoolean(false)
    val instructionID: ObservableInt = ObservableInt(3)
    val isFromOnboardinScreen: ObservableBoolean = ObservableBoolean(true)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isFinalPage: ObservableBoolean = ObservableBoolean(false)
    val isStartingPage: ObservableBoolean = ObservableBoolean(true)
    var toolbarTitle: ObservableField<String> = ObservableField("")
    var videoUrl: String=""
    var title: String=""
    var imagePath: String=""
    /**
     *[Function] ViewPager Next Button Click
     */
    fun onClickNextButton() {
        uiEvents.post(Event.OnNextButtonClicked)
    }

    /**
     *[Function]  watch video click
     */
    fun onClickPlayVideo() {
        isWatchVideoClicked.set(true)
        uiEvents.post(Event.OnPlayVideoClicked)
    }

    /**
     * [Function] ViewPager Previous Button Click
     */
    fun onClickPreviousButton() {
        uiEvents.post(Event.OnPreviousButtonClicked)
    }

    /**
     * [Function] Skip Tutorial text Clicked
     */
    fun onSkip() {
        uiEvents.post(Event.OnSkipTutorial)
    }

    /**
     * [Function] for fetching instruction data
     */
    fun fetchInstructionData() {
        disposable += getHowTodata.invoke(instructionID.get())
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    /**
     *[Function] Handling response after DB call
     */
    private fun handleFetchResult(result: Result<HowToData>) {
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                uiEvents.post(Event.OnDataUpdated)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    /**
     * [Function] Handling Erro response after DB call
     */
    private fun handleError(error: Error) {
        isErrorString.set(error.message)
        isShowError.set(true)
        uiEvents.post(Event.OnShowError)
    }

    fun onItemClick(videourl:String,titlem:String) {
        videoUrl = videourl
        title = titlem
        uiEvents.post(Event.OnItemClick)
    }

    fun onDoubleClick(imagePathm:String) {
        imagePath = imagePathm
        uiEvents.post(Event.OnSpinchAndZoom)
    }
    /**
     * Events for this view model
     */
    sealed class Event {
        object OnDataUpdated : Event()
        object OnShowError : Event()
        object OnSkipTutorial : Event()
        object OnNextButtonClicked : Event()
        object OnPreviousButtonClicked : Event()
        object OnPlayVideoClicked : Event()
        object OnItemClick : Event()
        object OnSpinchAndZoom : Event()
    }

}