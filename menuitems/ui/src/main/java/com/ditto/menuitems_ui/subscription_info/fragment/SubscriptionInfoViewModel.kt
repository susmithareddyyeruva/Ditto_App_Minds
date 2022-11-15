package com.ditto.menuitems_ui.subscription_info.fragment

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import com.ditto.menuitems.domain.SubscriptionInfoUsecase
import core.appstate.AppState
import core.event.UiEvents
import core.ui.BaseViewModel
import core.ui.common.Utility
import javax.inject.Inject

class SubscriptionInfoViewModel @Inject constructor(
    val context: Context,
    val subscriptionInfoUsecase: SubscriptionInfoUsecase
) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val subscriptionEndDateBase: ObservableField<String> = ObservableField()
    val subscriptionEndDateDesc: ObservableField<String> = ObservableField()
    val firstName: ObservableField<String> = ObservableField()
    val lastName: ObservableField<String> = ObservableField()
    val email: ObservableField<String> = ObservableField()
    val phone: ObservableField<String> = ObservableField()
    var errorString: ObservableField<String> = ObservableField("")

    init {
        setSubscriptionEndDateBase()
        setsubscriptionEndDateDesc()
        setEmail()
        setName()
        setPhoneNumber()
    }

    private fun setEmail() {
        email.set(": " + AppState.getEmail())
    }

    private fun setPhoneNumber() {
        phone.set(": " + AppState.getMobile())
    }

    private fun setName() {
        firstName.set(": " + AppState.getFirstName())
        lastName.set(": " + AppState.getLastName())
    }

    private fun setSubscriptionEndDateBase() {
        if (AppState.getSubDate()
                .isEmpty() || AppState.getSubDate() == null || AppState.isSubscriptionValid() == false
        ) {
            subscriptionEndDateBase.set(": 0 days left")
        } else {
            val days = Utility.getTotalNumberOfDays(AppState.getSubDate())
            subscriptionEndDateBase.set(": " + "$days days left")
        }
    }

    private fun setsubscriptionEndDateDesc() {
        Log.d("getSubscriptionStatus","getSubscriptionStatus: ${AppState.getSubscriptionStatus()}")
        if(AppState.getSubscriptionStatus().equals("Expired", true)){
            subscriptionEndDateDesc.set("Your subscription has EXPIRED. Please contact Customer Service to reactivate your subscription.")
        }else if(AppState.getSubscriptionStatus().equals("Paused", true)){
            subscriptionEndDateDesc.set("Your subscription has been PAUSED. Please contact Customer Service to reactivate your subscription.")
        }else{
            if (AppState.getSubDate()
                    .isEmpty() || AppState.getSubDate() == null || AppState.isSubscriptionValid() == false
            ) {
                subscriptionEndDateDesc.set("Your subscription will get EXPIRED in 0 days. Please contact Customer Service to reactivate your subscription.")
            } else {
                val days = Utility.getTotalNumberOfDays(AppState.getSubDate())
                subscriptionEndDateDesc.set("Your subscription will get EXPIRED in $days days. Please contact Customer Service to reactivate your subscription.")
            }
        }
    }

    fun onRenewSubClick() {
        uiEvents.post(Event.onRenewSubscriptionClick)
    }

    sealed class Event {
        object onRenewSubscriptionClick : Event()
    }
}