package com.ditto.splash.ui

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ditto.splash.domain.GetDbDataUseCase
import com.ditto.login.domain.LoginUser
import com.ditto.splash.domain.UpdateDbUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import com.ditto.storage.data.database.TraceDataDatabase
import com.ditto.storage.domain.StorageManager
import core.USER_EMAIL
import core.USER_FIRST_NAME
import core.USER_LAST_NAME
import core.USER_PHONE
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject


class SplashViewModel @Inject constructor(
    private val getDbUseCase: GetDbDataUseCase,
    private val updateDbUseCase: UpdateDbUseCase,
    val storageManager: StorageManager
) : BaseViewModel() {
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    private var errorString: ObservableField<String> = ObservableField("")
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    init {
        getUserDetails()
        GlobalScope.launch {
            delay(3000)
            fetchDbUser()
        }
        updateDb()
    }

    private fun updateDb() {
        disposable += updateDbUseCase.invoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun getUserDetails() {
        userEmail = storageManager.getStringValue(USER_EMAIL).toString()
        userPhone = storageManager.getStringValue(USER_PHONE).toString()
        userFirstName = storageManager.getStringValue(USER_FIRST_NAME).toString()
        userLastName = storageManager.getStringValue(USER_LAST_NAME).toString()
    }

    private fun fetchDbUser() {
        dbLoadError.set(false)
        disposable += getDbUseCase.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<LoginUser>) {
        when (result) {
            is Result.OnSuccess<LoginUser> -> {
                dbLoadError.set(false)
                Log.d(TraceDataDatabase.TAG, "- Success- ViewModel")
                if (result.data.userName?.isEmpty()!!) {
                    uiEvents.post(Event.NavigateToLogin)
                } else if (result.data.userName?.isNotEmpty()!! &&
                    result.data.dndOnboarding!!
                ) {
                    uiEvents.post(Event.NavigateToDashboard)
                } else if (result.data.userName?.isNotEmpty()!! &&
                    !result.data.dndOnboarding!!
                ) {
                    uiEvents.post(Event.NavigateToOnBoarding)
                }
            }
            is Result.OnError<LoginUser> -> handleError(result.error)
        }
    }

    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            else -> {
                dbLoadError.set(true)
                errorString.set(error.message)
            }
        }
    }

    sealed class Event {
        object NavigateToLogin : Event()
        object NavigateToOnBoarding : Event()
        object NavigateToDashboard : Event()
    }
}
