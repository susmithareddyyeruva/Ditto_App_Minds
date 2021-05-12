package com.ditto.home.ui

import androidx.databinding.ObservableField
import com.ditto.home.domain.model.HomeData
import com.ditto.storage.domain.StorageManager
import com.example.home_ui.R
import core.USER_FIRST_NAME
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(val storageManager: StorageManager) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val homeItem: ArrayList<HomeData> = ArrayList()
    var header: ObservableField<String> = ObservableField()

    sealed class Event {
        object OnClickMyPatterns : Event()
        object OnClickDitto : Event()
        object OnClickJoann : Event()
        object OnClickTutorial : Event()
    }

    init {
        setHomeHeader()
        setHomeItems()
    }

    fun onItemClick(id: Int) {
        when (id) {
            0 -> {
                uiEvents.post(Event.OnClickMyPatterns)
            }
            1 -> {
                uiEvents.post(Event.OnClickDitto)
            }
            2 -> {
                uiEvents.post(Event.OnClickJoann)
            }
            3 -> {
                uiEvents.post(Event.OnClickTutorial)
            }
        }
    }

    fun setHomeHeader() {
        if (storageManager.getStringValue(USER_FIRST_NAME) == null) {
            isGuest.set(true)
            header.set("Hi There,")
        } else {
            isGuest.set(false)
            header.set(storageManager.getStringValue(USER_FIRST_NAME).toString())
        }
    }

    fun setHomeItems() {
        val images = intArrayOf(
            R.drawable.ic_home_pattern_library, R.drawable.ic_home_ditto,
            R.drawable.ic_home_joann, R.drawable.ic_home_tutorial
        )

        val title = intArrayOf(
            R.string.pattern_library, R.string.more_patterns_available_at,
            R.string.for_fine_crafts_and_fabrics_visit_our_site, R.string.beam_setup_and_calibration
        )

        val description = intArrayOf(
            R.string.all_your_patterns_in_one_place, R.string.ditto_patterns_site,
            R.string.joann_site, R.string.view_tutorial
        )

        for (item in images.indices) {
            var homeItems: HomeData = HomeData(
                item,
                title[item],
                description[item],
                images[item],
            )
            homeItem.add(homeItems)
        }
    }

}
