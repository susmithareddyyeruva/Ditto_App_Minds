package com.ditto.menuitems_ui.customercare.fragment

import androidx.lifecycle.ViewModel
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class CustomerCareViewModel @Inject constructor() : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    fun PhoneClicked() {
        uiEvents.post(Event.OnPhoneClicked)
    }
    fun EmailClicked() {
        uiEvents.post(Event.OnEmailClicked)
    }

    sealed class Event {
        object OnPhoneClicked : Event()
        object OnEmailClicked : Event()
    }
}
