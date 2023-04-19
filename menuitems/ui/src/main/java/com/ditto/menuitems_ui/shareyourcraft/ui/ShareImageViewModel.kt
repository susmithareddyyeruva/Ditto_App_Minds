package com.ditto.menuitems_ui.shareyourcraft.ui

import androidx.databinding.ObservableBoolean
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class ShareImageViewModel @Inject constructor() : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isShareButtonVisible: ObservableBoolean = ObservableBoolean(false)
    val isCameraVisible: ObservableBoolean = ObservableBoolean(false)

    sealed class Event {
        object OnPhotoGalleryClicked : Event()
        object OnOpenCameraClicked : Event()
        object OnShareImageClicked : Event()
        object OnCameraButtonClicked : Event()
    }

    fun onOpenCameraClick() {
        uiEvents.post(Event.OnOpenCameraClicked)
    }

    fun onPhotoGalleryClick() {
        uiEvents.post(Event.OnPhotoGalleryClicked)
    }

    fun onShareImageClick() {
        uiEvents.post(Event.OnShareImageClicked)
    }

    fun onTakePhoto() {
        uiEvents.post(Event.OnCameraButtonClicked)
    }
}