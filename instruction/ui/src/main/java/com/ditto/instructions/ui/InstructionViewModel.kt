package com.ditto.instructions.ui
/**
 * Created by Vishnu A V on  03/08/2020.
 * Viewmodel class representing Beamsetup and Calibration values
 */
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.instructions.domain.GetInstructionDataUsecase
import com.ditto.instructions.domain.model.InstructionsData
import core.event.UiEvents
import core.ui.BaseViewModel
import core.whileSubscribed
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class InstructionViewModel @Inject constructor(
    private val getInstructiondata: GetInstructionDataUsecase
) : BaseViewModel() {
    val isShowindicator: ObservableBoolean = ObservableBoolean(true)
    val isFinalPage: ObservableBoolean = ObservableBoolean(true)
    val isWatchVideoClicked: ObservableBoolean = ObservableBoolean(false)
    val isStartingPage: ObservableBoolean = ObservableBoolean(true)
    val isErrorString: ObservableField<String> = ObservableField("")
    val isShowError: ObservableBoolean = ObservableBoolean(false)
    val isFromCameraScreen: ObservableBoolean = ObservableBoolean(false)
    val isFromHome: ObservableBoolean = ObservableBoolean(false)
    val isDSSchecked: ObservableBoolean = ObservableBoolean(false)
    val instructionID: ObservableInt = ObservableInt(1)
    val tabPosition: ObservableInt = ObservableInt(0)
    var data: MutableLiveData<InstructionsData> = MutableLiveData()
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var toolbarTitle: ObservableField<String> = ObservableField("")
    /**
     * [Function] for fetching instruction data
     */
    fun fetchInstructionData() {
        disposable += getInstructiondata.invoke(instructionID.get())
            .delay(600, TimeUnit.MILLISECONDS)
            .whileSubscribed { showProgress(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }
    private fun showProgress(process: Boolean) {
        isShowindicator.set(process)
    }
    /**
     *[Function] Handling response after DB call
     */
    private fun handleFetchResult(result: Result<InstructionsData>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
                isShowindicator.set(false)
                uiEvents.post(Event.OnInstructionDataUpdated)
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
    /**
     * [Function] Skip Tutoial text Clicked
     */
    fun onSkip() {
        uiEvents.post(Event.OnSkipTutorial)
    }

    /**
     *[Function] ViewPager Next Button Click
     */
    fun onClickNextButton() {
        uiEvents.post(Event.OnNextButtonClicked)
    }

    /**
     * [Function] ViewPager Previous Button Click
     */
    fun onClickPreviousButton() {
        uiEvents.post(Event.OnPreviousButtonClicked)
    }

    /**
     * [Function] Go to calibration text Click
     */
    fun onClickCalibrationStepsButton() {

        uiEvents.post(Event.OnCalibrationStepsButtonClicked)
    }

    /**
     * [Function] HOW TO text  Click
     */
    fun onClickHowToButton() {
        uiEvents.post(Event.OnHowToButtonClicked)
    }

    /**
     *[Function]  watch video click
     */
    fun onClickPlayVideo() {
        isWatchVideoClicked.set(true)
        uiEvents.post(Event.OnPlayVideoClicked)
    }

    /**
     * [Function] Calibration Button Clicked
     */
    fun onCalibrationButtonClicked() {

        uiEvents.post(Event.OnCalibrationButtonClicked)
    }

    /**
     * Events for this view model
     */
    sealed class Event {
        /**
         * Event emitted by [events] when the data received successfully
         */
        object OnInstructionDataUpdated : Event()

        /**
         * Event emitted by [events] when next button clicked
         */
        object OnNextButtonClicked : Event()

        /**
         * Event emitted by [events] when previous button clicked
         */
        object OnPreviousButtonClicked : Event()

        /**
         * Event emitted by [events] when Calibration text Clicked
         */
        object OnCalibrationStepsButtonClicked : Event()

        /**
         * Event emitted by [events] when HowTo text Clicked
         */
        object OnHowToButtonClicked : Event()

        /**
         * Event emitted by [events] when watch video clicked
         */
        object OnPlayVideoClicked : Event()

        /**
         * Event emitted by [events] when Error occurs
         */
        object OnShowError : Event()

        /**
         * Event emitted by [events] when Error occurs
         */
        object OnCalibrationButtonClicked : Event()
        /**
         * Event emitted by [events] when skip tutorial clicked
         */
        object OnSkipTutorial : Event()

        object OnHideProgress : Event()
        object OnShowProgress : Event()

    }

}
