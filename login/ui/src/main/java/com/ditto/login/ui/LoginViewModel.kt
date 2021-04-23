package com.ditto.login.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.domain.GetLoginDbUseCase
import com.ditto.login.domain.LoginInputData
import com.ditto.login.domain.LoginResultDomain
import com.ditto.login.domain.LoginUser
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import non_core.lib.error.RemoteConfigError
import javax.inject.Inject


class LoginViewModel @Inject constructor(
    private val context: Context,
    val loggerFactory: LoggerFactory,
    val useCase: GetLoginDbUseCase
) : BaseViewModel() {

    var userName: ObservableField<String> = ObservableField<String>("")
    var password: ObservableField<String> = ObservableField<String>("")
    var versionName: ObservableField<String> = ObservableField<String>("")
    val isEmailValidated: ObservableBoolean = ObservableBoolean(true)
    val isPasswordValidated: ObservableBoolean = ObservableBoolean(true)
    val loadingIndicator: ObservableBoolean = ObservableBoolean(false)
    var errorString: ObservableField<String> = ObservableField("")
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    val logger: Logger by lazy {
        loggerFactory.create(LoginViewModel::class.java.simpleName)
    }

    fun validateCredentials() {
        isEmailValidated.set(true)
        isPasswordValidated.set(true)
        if (TextUtils.isEmpty(userName.get()) || !isEmailValid()) {
            isEmailValidated.set(false)
            logger.d("username invalid")
        } else if (TextUtils.isEmpty(password.get())) {
            isPasswordValidated.set(false)
            logger.d("password invalid")
        } else {
            isEmailValidated.set(true)
            isPasswordValidated.set(true)

            //Making api call for Login
            loadingIndicator.set(true)
            disposable += useCase.loginUserWithCredential(
                LoginInputData(userName.get(), password.get())
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { handlLoginResult(it) }
        }
    }

    //redirecting to external browser
    fun openExternalBrowser() {
        val url = "https://www.joann.com/create-account"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun handlLoginResult(result: Result<LoginResultDomain>) {
        logger.d("handleFetchResult ${result.toString()}")
        loadingIndicator.set(false)
        when (result) {
            is Result.OnSuccess -> {
                if (result.data.auth_type.equals("registered")) {

                    disposable += useCase.createUser(
                        LoginUser(
                            userName = userName.get(),
                            isLoggedIn = true
                        )
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy { handleFetchResult(it) }
                }

            }
            is Result.OnError ->
                //handleError(Error("", null))
                handleError(result.error)


        }
    }

    private fun handleFetchResult(result: Any) {
        logger.d("handleFetchResult ${result.toString()}")
        uiEvents.post(Event.OnLoginClicked)
    }

    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)
            is RemoteConfigError -> Log.d(
                "LoginViewModel",
                "Remote Config fetch error : ${error.message}"
            )
            else -> {

                errorString.set(error.message)
                uiEvents.post(Event.OnLoginFailed)
            }


        }
    }

    private fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(userName.get()).matches()
    }

    /**
     * Events for this view model
     */
    sealed class Event {
        object OnLoginClicked : Event()

        /**
         * Event emitted by [events] when the data updated successfully
         */
        object OnLoginFailed : Event()
    }
}
