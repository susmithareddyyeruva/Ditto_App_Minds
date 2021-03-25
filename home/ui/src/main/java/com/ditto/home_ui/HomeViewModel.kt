package com.ditto.home_ui

import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(): BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()


    sealed class Event {
        object OnClickBuyPattern : Event()
        object OnClickJoann : Event()
        object OnClickResumeRecent : Event()
    }

    fun onClickBuyPattern(){
        uiEvents.post(Event.OnClickBuyPattern)
    }

    fun onClickResume(){
        uiEvents.post(Event.OnClickResumeRecent)
    }

    fun onClickShop(){
        uiEvents.post(Event.OnClickJoann)
    }
}
