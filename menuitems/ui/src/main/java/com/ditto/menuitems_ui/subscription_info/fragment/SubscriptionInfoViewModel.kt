package com.ditto.menuitems_ui.subscription_info.fragment

import android.content.Context
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
    val name: ObservableField<String> = ObservableField()
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
        name.set(": " + AppState.getFirstName() + " " + AppState.getLastName())
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
        if (AppState.getSubDate()
                .isEmpty() || AppState.getSubDate() == null || AppState.isSubscriptionValid() == false
        ) {
            subscriptionEndDateDesc.set("Your subscription will get expired in 0 days. Please renew the subscription to continue working on it.")

        } else {
            val days = Utility.getTotalNumberOfDays(AppState.getSubDate())
            subscriptionEndDateDesc.set("Your subscription will get expired in $days days. Please renew the subscription to continue working on it.")

        }
    }

    fun onRenewSubClick() {
        uiEvents.post(Event.onRenewSubscriptionClick)
    }

    sealed class Event {
        object onRenewSubscriptionClick : Event()
    }
}