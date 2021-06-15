package com.ditto.videoplayer

import androidx.databinding.ObservableBoolean
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class VideoPlayerViewModel @Inject constructor() : BaseViewModel() {


    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isPlayButtonVisible: ObservableBoolean = ObservableBoolean(false)
    var videoUrl: String = ""
    var title: String = ""
    var from: String = ""

    fun onPlayButtonClicked() {
        uiEvents.post(Event.OnPlayButtonClicked)
        isPlayButtonVisible.set(false)
    }

    fun onSkipButtonClicked() {
        uiEvents.post(Event.OnSkipButtonClicked)
    }

    fun onCloseButtonClicked() {
        uiEvents.post(Event.OnCloseButtonClicked)
    }

    sealed class Event {
        /**
         * Event emitted by [events] when the playButton is clicked
         */
        object OnPlayButtonClicked : Event()

        /**
         * Event emitted by [events] when the skip is clicked
         */
        object OnSkipButtonClicked : Event()

        /**
         * Event emitted by [events] when the close is clicked
         */
        object OnCloseButtonClicked : Event()
    }
}