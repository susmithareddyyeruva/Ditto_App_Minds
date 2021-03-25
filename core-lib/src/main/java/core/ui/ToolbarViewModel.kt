package core.ui

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import core.event.UiEvents
import javax.inject.Inject

class ToolbarViewModel @Inject constructor(): BaseViewModel() {
    val visibility = ObservableBoolean(false)

    val isShowActionBar = ObservableBoolean(false)
    val isShowTransparentActionBar = ObservableBoolean(false)
    var toolbarTitle: ObservableField<String> = ObservableField("")
    var isShowSkip: ObservableBoolean = ObservableBoolean(false)
    var isShowMenu: ObservableBoolean = ObservableBoolean(false)
    var isShowActionMenu: ObservableBoolean = ObservableBoolean(false)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    fun onSkip() {
        uiEvents.post(Event.OnSkipTutorial)
    }

    fun onMenu() {
        uiEvents.post(Event.onMenu)
    }



    /**
     * Events for this view model
     */
    sealed class Event {
        /**
         * Event emitted by [events] when the data received successfully
         */
        object OnSkipTutorial : Event()

        object onMenu : Event()


    }
}