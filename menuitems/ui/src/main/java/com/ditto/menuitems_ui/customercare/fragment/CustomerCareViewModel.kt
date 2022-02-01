package com.ditto.menuitems_ui.customercare.fragment

import android.content.Context
import androidx.databinding.ObservableField
import com.ditto.storage.domain.StorageManager
import core.CUSTOMERCARE_EMAIL
import core.CUSTOMERCARE_PHONE
import core.CUSTOMERCARE_TIMING
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject


class CustomerCareViewModel @Inject constructor(val context : Context,val storageManager: StorageManager) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val mobileNumberValue: ObservableField<String> = ObservableField()
    val supportTimingsValue: ObservableField<String> = ObservableField()
    init {
        getEmailId()
        setMobileNumber()
        setTimings()
    }
    fun  getEmailId(): String {
        return storageManager.getStringValue(CUSTOMERCARE_EMAIL).toString()

    }

    private fun setTimings(){
        supportTimingsValue.set(storageManager.getStringValue(CUSTOMERCARE_TIMING))
    }

    private fun setMobileNumber(){
        mobileNumberValue.set(storageManager.getStringValue(CUSTOMERCARE_PHONE))
    }

    fun phoneClicked() {
        uiEvents.post(Event.OnPhoneClicked)
    }
    fun emailClicked() {
        uiEvents.post(Event.OnEmailClicked)
    }

    sealed class Event {
        object OnPhoneClicked : Event()
        object OnEmailClicked : Event()
    }
}
