package com.ditto.calibration.ui
/**
 * Created by Vishnu A V on  10/08/2020.
 * View Model for Calibration
 */
import androidx.databinding.ObservableBoolean
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class CalibrationViewModel @Inject constructor()  : BaseViewModel() {

    val isShowFinalImage: ObservableBoolean = ObservableBoolean(false)
    val isShowCameraView: ObservableBoolean = ObservableBoolean(true)
    val isShowCameraButton: ObservableBoolean = ObservableBoolean(true)
    val isProgressLoading: ObservableBoolean = ObservableBoolean(true)
    val isShowDialog: ObservableBoolean = ObservableBoolean(false)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    /**
     * [Function] Camera button click
     */
    fun onTakePhoto() {
        uiEvents.post(Event.OnTakePhotoClicked)
    }

    /**
     * [Function] Instruction text Clicked
     */
    fun onInstructionTextClicked() {
        uiEvents.post(Event.OnInstructionClicked)
    }

    /**
     * [events] Events for this view model
     */
    sealed class Event {

        /**
         * Event emitted by [events] on Camera button click
         */
        object OnTakePhotoClicked : Event()
        /**
         * Event emitted by [events] on Instruction text click
         */
        object OnInstructionClicked : Event()

    }
}