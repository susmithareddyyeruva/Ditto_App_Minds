package com.ditto.videoplayer

import androidx.databinding.ObservableBoolean
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class VideoPlayerViewModel @Inject constructor() : BaseViewModel() {


    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isPlayButtonVisible: ObservableBoolean = ObservableBoolean(true)

    fun onPlayButtonClicked() {
        uiEvents.post(Event.OnPlayButtonClicked)
        isPlayButtonVisible.set(false)
    }

    sealed class Event {
        /**
         * Event emitted by [events] when the data received successfully
         */
        object OnPlayButtonClicked : Event()
    }
}