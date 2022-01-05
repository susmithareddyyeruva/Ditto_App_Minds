package com.ditto.tutorial.ui

import androidx.databinding.ObservableInt
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class TutorialViewModel @Inject constructor() : BaseViewModel() {


    val clickID: ObservableInt = ObservableInt(1)

    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    fun onClickBeamsetup() {
        clickID.set(1)
        uiEvents.post(Event.OnItemClick)
    }

    fun onClickCalibration() {
        clickID.set(2)
        uiEvents.post(Event.OnItemClick)
    }

    fun onClickHowto() {
        clickID.set(3)
        uiEvents.post(Event.OnHowToClick)
    }


    sealed class Event {
        object OnItemClick : Event()

        object OnHowToClick : Event()
    }
}