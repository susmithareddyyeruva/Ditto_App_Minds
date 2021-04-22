package com.ditto.login.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.domain.GetLoginDbUseCase
import com.ditto.login.domain.LoginUser
import core.event.UiEvents
import core.ui.BaseViewModel
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.error.NoNetworkError
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
        } else if (TextUtils.isEmpty(password.get()) || !isPasswordValid()) {
            isPasswordValidated.set(false)
            logger.d("password invalid")
        } else {
            isEmailValidated.set(true)
            isPasswordValidated.set(true)
            disposable += useCase.createUser(
                LoginUser(
                    userName = userName.get(),
                    isLoggedIn = true
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { handleFetchResult(it) }


            //Making api call for Login

            disposable += useCase.userLogin(
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



    //redirecting to external browser
    fun signUpRedirection(){
        Utility.redirectToExternalBrowser(context, BuildConfig.SIGN_UP_URL)
    }

    fun forgotPasswordRedirection(){
        Utility.redirectToExternalBrowser(context, BuildConfig.FORGOT_PASSWORD_URL)

    }

    private fun handleFetchResult(result: Any) {
        logger.d("handleFetchResult ${result.toString()}")
        if(result.toString() == "0")
        uiEvents.post(Event.OnLoginClicked)
        else
            handleError(Error("",null))
    }

    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> activeInternetConnection.set(false)

        }
    }

    private fun isEmailValid(): Boolean {
        return userName.get().equals("Ditto")
    }

    private fun isPasswordValid(): Boolean {
        return password.get().equals("Ditto")
    }
    /**
     * Events for this view model
     */
    sealed class Event {
        object OnLoginClicked : Event()
    }
}
