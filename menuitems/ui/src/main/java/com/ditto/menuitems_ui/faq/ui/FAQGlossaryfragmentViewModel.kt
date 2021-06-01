package com.ditto.menuitems_ui.faq.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ditto.menuitems_ui.faq.ui.json.JsonHelper
import com.ditto.menuitems_ui.faq.ui.models.FAQGlossaryResponse
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject

class FAQGlossaryfragmentViewModel @Inject constructor(val context: Context) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    var data: MutableLiveData<FAQGlossaryResponse> = MutableLiveData()
    val events = uiEvents.stream()

    fun fetchData() {
        data.value = context?.let {
            JsonHelper(it).getFAQDataMain()

        }


    }

    sealed class Event {
        object onDataUpdated : Event()
    }
}

