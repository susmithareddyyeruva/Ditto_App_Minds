package com.ditto.menuitems_ui.customercare.fragment

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.databinding.ObservableField
import com.ditto.menuitems_ui.R
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

    fun setTimings(){
        supportTimingsValue.set(storageManager.getStringValue(CUSTOMERCARE_TIMING))
    }

    fun setMobileNumber(){
        mobileNumberValue.set(storageManager.getStringValue(CUSTOMERCARE_PHONE))
    }

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
