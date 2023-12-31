package com.ditto.login.ui

import android.content.Context
import androidx.core.util.PatternsCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.domain.model.*
import com.ditto.storage.domain.StorageManager
import core.*
import core.appstate.AppState
import core.event.UiEvents
import core.lib.BuildConfig
import core.ui.BaseViewModel
import core.ui.common.Utility
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
    val useCase: GetLoginDbUseCase,
    val storageManager: StorageManager,
    val utility: Utility
) : BaseViewModel() {

    var userName: ObservableField<String> = ObservableField<String>("")
    var password: ObservableField<String> = ObservableField<String>("")
    var versionName: ObservableField<String> = ObservableField<String>("")
    val isEmailValidated: ObservableBoolean = ObservableBoolean(true)
    val isPasswordValidated: ObservableBoolean = ObservableBoolean(true)
    val isLoginButtonFocusable: ObservableBoolean = ObservableBoolean(true)
    var errorString: ObservableField<String> = ObservableField("")
    var videoUrl: String = ""
    var imageUrl: ObservableField<String> = ObservableField("")
    val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    var viewPagerData: MutableLiveData<List<LoginViewPagerData>> = MutableLiveData()
    var viewPagerDataList: MutableLiveData<List<String>> = MutableLiveData()
    val logger: Logger by lazy {
        loggerFactory.create(LoginViewModel::class.java.simpleName)
    }

    fun validateCredentials() {
        isEmailValidated.set(true)
        isPasswordValidated.set(true)
        if (userName.get().isNullOrEmpty() || !isEmailValid()) {
            isEmailValidated.set(false)
            logger.d("username invalid")
        } else if (password.get().isNullOrEmpty()) {
            isPasswordValidated.set(false)
            uiEvents.post(Event.HidePasswordEyeToggleIcon)
            logger.d("password invalid")
        } else {
            isEmailValidated.set(true)
            isPasswordValidated.set(true)

            //Making api call for Login
            uiEvents.post(Event.OnShowProgress)
            makeUserLoginApiCall()
            isLoginButtonFocusable.set(false)

        }
    }

    internal fun makeUserLoginApiCall() {
        disposable += useCase.loginUserWithCredential(
            LoginInputData(
                userName.get(),
                password.get()
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    fun signUpRedirection() {
        Utility.redirectToExternalBrowser(context, BuildConfig.SIGN_UP_URL)
    }

    private fun handleFetchResult(result: Result<LoginResultDomain>) {
        logger.d("handleFetchResult ${result.toString()}")
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                if (result.data.faultDomain == null) {
                    isLoginButtonFocusable.set(true)

                    //User login successfull
                    storageManager.savePrefs(USER_EMAIL, result.data.email ?: "")
                    storageManager.savePrefs(USER_PHONE, result.data.phoneHome ?: "")
                    storageManager.savePrefs(USER_FIRST_NAME, result.data.firstName ?: "")
                    storageManager.savePrefs(USER_LAST_NAME, result.data.lastName ?: "")
                    storageManager.savePrefs(SPLICE_REMINDER, result.data.cSpliceReminder)
                    storageManager.savePrefs(MIRROR_REMINDER, result.data.cMirrorReminder)
                    storageManager.savePrefs(RECIEVER_EMAIL, result.data.cReceiveEmail)
                    storageManager.savePrefs(SAVE_CALIBRATION_PHOTOS, result.data.cSaveCalibrationPhotos)

                    AppState.setCustID(result.data.customerId ?: "")
                    AppState.setCustNumber(result.data.customerNo ?: "")
                    AppState.setEmail(result.data.email ?: "")
                    AppState.setFirstName(result.data.firstName ?: "")
                    AppState.setLastName(result.data.lastName ?: "")
                    AppState.setSubscriptionDate(result.data.cSubscriptionPlanEndDate ?: "")
                    AppState.setSubscriptionStatus(result.data.cSubscriptionStatus ?: "")
                    AppState.setSubscriptionValidity(result.data.cSubscriptionValid ?: false)
                    AppState.setMobile(result.data.phoneHome ?: "")
                    AppState.saveKey(result.data.cEncryptionkey ?: "")
                    storageManager.savePrefs(
                        SPLICE_CUT_COMPLETE_REMINDER,
                        result.data.cSpliceCutCompleteReminder
                    )
                    storageManager.savePrefs(
                        MULTIPLE_PIECE_REMINDER,
                        result.data.cSpliceMultiplePieceReminder
                    )

                    userEmail = result.data.email ?: ""
                    userPhone = result.data.phoneHome ?: ""
                    userFirstName = result.data.firstName ?: ""
                    userLastName = result.data.lastName ?: ""
                    subscriptionEndDate = result.data.cSubscriptionPlanEndDate ?: ""
                    c_subscriptionStatus = result.data.cSubscriptionStatus ?: ""
                    AppState.setIsLogged(true)
                    /**
                     * Storing the subscription information into DB
                     */
                    makeCreateUserCall(result)

                } else { //http status code is 200  also have error
                    isLoginButtonFocusable.set(true)
                    errorString.set(result.data.faultDomain?.message ?: "")
                    uiEvents.post(Event.OnLoginFailed)
                }

            }
            is Result.OnError -> {
                isLoginButtonFocusable.set(true)
                handleError(result.error)
                uiEvents.post(Event.OnHideProgress)
            }
        }
    }

     fun makeCreateUserCall(result: Result.OnSuccess<LoginResultDomain>) {
        disposable += useCase.createUser(
            LoginUser(
                userName = userName.get(),
                _type = result.data._type,
                auth_type = result.data.authType,
                customer_id = result.data.customerId,
                customer_no = result.data.customerNo,
                email = result.data.email,
                first_name = result.data.firstName,
                last_name = result.data.lastName,
                last_visit_time = result.data.lastVisitTime,
                last_modified = result.data.lastModified,
                last_login_time = result.data.lastLoginTime,
                gender = result.data.gender,
                phone_home = result.data.phoneHome,
                login = result.data.phoneHome,
                previous_login_time = result.data.previousLoginTime,
                previous_visit_time = result.data.previousVisitTime,
                salutation = result.data.salutation,
                isLoggedIn = true,
                cMirrorReminder = result.data.cMirrorReminder,
                cReceiveEmail = result.data.cReceiveEmail,
                cSpliceCutCompleteReminder = result.data.cSpliceCutCompleteReminder,
                cSpliceMultiplePieceReminder = result.data.cSpliceMultiplePieceReminder,
                cSpliceReminder = result.data.cSpliceReminder,
                cCuttingReminder = result.data.cCuttingReminder,
                cSaveCalibrationPhotos = result.data.cSaveCalibrationPhotos,
                cInitialisationVector = result.data.cInitialisationVector,
                cVectorKey = result.data.cVectorKey,
                cSubscriptionValid = result.data.cSubscriptionValid,
                cSubscriptionPlanEndDate = result.data.cSubscriptionPlanEndDate,
                cSubscriptionPlanStartDate = result.data.cSubscriptionPlanStartDate,
                cSubscriptionPlanPrice = result.data.cSubscriptionPlanPrice,
                cSubscriptionPlanId = result.data.cSubscriptionPlanId,
                cSubscriptionPlanName = result.data.cSubscriptionPlanName,
                cSubscriptionID = result.data.cSubscriptionID,
                cSubscriptionPlanCurrency = result.data.cSubscriptionPlanCurrency,
                cSubscriptionType = result.data.cVectorKey,
                cSubscriptionPlanBillingEndDate = result.data.cSubscriptionPlanBillingEndDate,
                cSubscriptionPlanBillingStartDate = result.data.cSubscriptionPlanBillingStartDate

            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    fun forgotPasswordRedirection() {
        Utility.redirectToExternalBrowser(context, BuildConfig.FORGOT_PASSWORD_URL)

    }

    private fun handleFetchResult(result: Any) {
        logger.d("handleFetchResult ${result.toString()}")
        uiEvents.post(Event.OnLoginClicked)
    }

    internal fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                errorString.set(error.message)
                uiEvents.post(Event.OnLoginFailed)
            }
            is RemoteConfigError -> logger.d(
                "LoginViewModel, Remote Config fetch error : ${error.message}"
            )
            else -> {
                errorString.set(error.message)
                uiEvents.post(Event.OnLoginFailed)
            }

        }
    }

    internal fun isEmailValid(): Boolean {
        return PatternsCompat.EMAIL_ADDRESS.matcher(userName.get()).matches()
    }

    fun guestLogin() {
        uiEvents.post(Event.OnGuestPreviewClicked)
    }

    fun deleteUserInfo() {
        disposable += useCase.deleteDbUser(storageManager.getStringValue(USER_EMAIL) ?: "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
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

        /**
         * Event emitted by [events] when the data updated successfully
         */
        object OnGuestPreviewClicked : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object OnLandingSuccess : Event()
        object HidePasswordEyeToggleIcon : Event()
    }

    fun getLandingScreenDetails() {
        uiEvents.post(Event.OnShowProgress)
        disposable += useCase.getLandingContentDetails().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy {
                handleLandingScreenFetchDetails(it)
            }
    }

    internal fun handleLandingScreenFetchDetails(it: Result<LandingContentDomain>?) {
        logger.d("LandingDetails  : ${it.toString()}")
        when (it) {
            is Result.OnSuccess -> {
                /**
                 * Saving Customer Care Information's
                 */
                uiEvents.post(Event.OnHideProgress)
                storageManager.savePrefs(CUSTOMERCARE_EMAIL, it.data.c_body.customerCareEmail)
                storageManager.savePrefs(CUSTOMERCARE_PHONE, it.data.c_body.customerCareePhone)
                storageManager.savePrefs(CUSTOMERCARE_TIMING, it.data.c_body.customerCareeTiming)
                storageManager.savePrefs(VIDEO_URL, it.data.c_body.videoUrl)
                videoUrl = it.data.c_body.videoUrl
                imageUrl.set(it.data.c_body.imageUrl)
                uiEvents.post(Event.OnLandingSuccess)
            }
            is Result.OnError -> handleError(it.error)
            else -> {
                uiEvents.post(Event.OnHideProgress)

            }
        }
    }
}

