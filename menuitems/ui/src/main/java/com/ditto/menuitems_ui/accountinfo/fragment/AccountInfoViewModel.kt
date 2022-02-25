package com.ditto.menuitems_ui.accountinfo.fragment

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.data.error.AccountInfoFetchError
import com.ditto.menuitems.domain.AccountInfoUsecase
import com.ditto.menuitems.domain.model.AccountInfoDomain
import core.ERROR_FETCH
import core.appstate.AppState
import core.event.UiEvents
import core.ui.BaseViewModel
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import javax.inject.Inject


class AccountInfoViewModel @Inject constructor(
    val context: Context,
    val accountInfoUsecase: AccountInfoUsecase
) : BaseViewModel() {
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val subscriptionEndDateBase: ObservableField<String> = ObservableField()
    val name: ObservableField<String> = ObservableField()
    val email: ObservableField<String> = ObservableField()
    val phone: ObservableField<String> = ObservableField()
    var errorString: ObservableField<String> = ObservableField("")

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(AccountInfoViewModel::class.java.simpleName)
    }


    init {
        setSubscriptionEndDateBase()
        setEmail()
        setName()
        setPhoneNumber()
    }

    fun deleteAccount() {
        disposable += accountInfoUsecase.deleteAccount(AppState.getCustNO())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleResult(it) }
    }

    private fun handleResult(result: Result<AccountInfoDomain>) {
        when (result) {
            is Result.OnSuccess -> {
                logger.d("Success" + result.data)
            }
            is Result.OnError -> {
                logger.d("Failed")
                handleError(result.error)
            }
        }
    }

    //error handler for data fetch related flow
    fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                errorString.set(error.message)
                uiEvents.post(Event.NoInternet)
            }
            is AccountInfoFetchError -> {
                if (error.message.contentEquals("No such element exception")) {
                    uiEvents.post(Event.onLogout)
                }else{
                    errorString.set("${error.message?: ERROR_FETCH}\n Unable to delete Account. Please contact customer care.")
                    uiEvents.post(Event.OnResultFailed)
                }
            }
            else -> {
                errorString.set("${error.message?: ERROR_FETCH}\n Unable to delete Account. Please contact customer care.")
                uiEvents.post(Event.OnResultFailed)
            }

        }
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
                .isEmpty() || AppState.getSubDate() == null
        ) {
            subscriptionEndDateBase.set(": 0 days left")
        } else {
            val days = Utility.getTotalNumberOfDays(AppState.getSubDate())
            subscriptionEndDateBase.set(": "+"$days days left")
        }
    }

    fun onDeleteAccountClick() {
        uiEvents.post(Event.onDeleteAccountClick)
    }

    sealed class Event {
        object onDeleteAccountClick : Event()
        object onLogout : Event()
        object NoInternet : Event()
        object OnResultFailed : Event()
    }
}
