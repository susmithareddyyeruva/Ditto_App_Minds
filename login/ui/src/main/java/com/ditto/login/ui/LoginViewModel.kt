package com.ditto.login.ui

import android.content.Context
import android.text.TextUtils
import android.util.Log
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
    val storageManager: StorageManager
) : BaseViewModel() {

    var userName: ObservableField<String> = ObservableField<String>("")
    var password: ObservableField<String> = ObservableField<String>("")
    var versionName: ObservableField<String> = ObservableField<String>("")
    val isEmailValidated: ObservableBoolean = ObservableBoolean(true)
    val isPasswordValidated: ObservableBoolean = ObservableBoolean(true)
    val isLoginButtonFocusable: ObservableBoolean = ObservableBoolean(true)
    var errorString: ObservableField<String> = ObservableField("")
    var videoUrl: String=""
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    var viewPagerData: MutableLiveData<List<LoginViewPagerData>> = MutableLiveData()
    var viewPagerDataList: MutableLiveData<List<String>> = MutableLiveData()
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
            uiEvents.post(Event.OnShowProgress)
            disposable += useCase.loginUserWithCredential(
                LoginInputData(
                    userName.get(),
                    password.get()
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { handleFetchResult(it) }
            isLoginButtonFocusable.set(false)

        }
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
                    storageManager.savePrefs(USER_PHONE, result.data.phone_home ?: "")
                    storageManager.savePrefs(USER_FIRST_NAME, result.data.first_name ?: "")
                    storageManager.savePrefs(USER_LAST_NAME, result.data.last_name ?: "")
                    storageManager.savePrefs(SPLICE_REMINDER, result.data.cSpliceReminder)
                    storageManager.savePrefs(MIRROR_REMINDER, result.data.cMirrorReminder)
                    storageManager.savePrefs(RECIEVER_EMAIL, result.data.cReceiveEmail)
                    AppState.setCustID(result.data.customer_id!!)
                    storageManager.savePrefs(
                        SPLICE_CUT_COMPLETE_REMINDER,
                        result.data.cSpliceCutCompleteReminder
                    )
                    storageManager.savePrefs(
                        MULTIPLE_PIECE_REMINDER,
                        result.data.cSpliceMultiplePieceReminder
                    )
                    /**
                     * Storing the subscription information into Local Cache
                     */

                    storageManager.savePrefs(
                        CSUBSCRIPTION_ENDDATE,
                        result.data.c_subscriptionPlanEndDate
                    )
                    storageManager.savePrefs(
                        CSUBSCRIPTION_VALID,
                        result.data.c_subscriptionValid
                    )

                    userEmail = result.data.email ?: ""
                    userPhone = result.data.phone_home ?: ""
                    userFirstName = result.data.first_name ?: ""
                    userLastName = result.data.last_name ?: ""
                    AppState.setIsLogged(true)
                    /**
                     * Storing the subscription information into DB
                     */
                    disposable += useCase.createUser(
                        LoginUser(
                            userName = userName.get(),
                            _type = result.data._type,
                            auth_type = result.data.auth_type,
                            customer_id = result.data.customer_id,
                            customer_no = result.data.customer_no,
                            email = result.data.email,
                            first_name = result.data.first_name,
                            last_name = result.data.last_name,
                            last_visit_time = result.data.last_visit_time,
                            last_modified = result.data.last_modified,
                            last_login_time = result.data.last_login_time,
                            gender = result.data.gender,
                            phone_home = result.data.phone_home,
                            login = result.data.phone_home,
                            previous_login_time = result.data.previous_login_time,
                            previous_visit_time = result.data.previous_visit_time,
                            salutation = result.data.salutation,
                            isLoggedIn = true,
                            cMirrorReminder = result.data.cMirrorReminder,
                            cReceiveEmail = result.data.cReceiveEmail,
                            cSpliceCutCompleteReminder = result.data.cSpliceCutCompleteReminder,
                            cSpliceMultiplePieceReminder = result.data.cSpliceMultiplePieceReminder,
                            cSpliceReminder = result.data.cSpliceReminder,
                            cCuttingReminder = result.data.cCuttingReminder,
                            cInterestArt = result.data.cInterestArt,
                            cInterestBridalSpecialOccasionProjects = result.data.cInterestBridalSpecialOccasionProjects,
                            cInterestClassroomCraftsDecor = result.data.cInterestClassroomCraftsDecor,
                            cInterestFloral = result.data.cInterestFloral,
                            cInterestFoodCrafts = result.data.cInterestFoodCrafts,
                            cInterestHolidayPartyDecorating = result.data.cInterestHolidayPartyDecorating,
                            cInterestHomeDecor = result.data.cInterestHomeDecor,
                            cInterestJewelry = result.data.cInterestJewelry,
                            cInterestKidsCrafts = result.data.cInterestKidsCrafts,
                            cInterestKnittingCrochet = result.data.cInterestKnittingCrochet,
                            cInterestPaperCrafts = result.data.cInterestPaperCrafts,
                            cInterestQuiltingSewingFabric = result.data.cInterestQuiltingSewingFabric,
                            cIsCostumeGuildEnrolled = result.data.cIsCostumeGuildEnrolled,
                            cIsFourHEnrolled = result.data.cIsFourHEnrolled,
                            cIsGirlScoutsEnrolled = result.data.cIsGirlScoutsEnrolled,
                            cIsJoannPlusCustomer = result.data.cIsJoannPlusCustomer,
                            cIsMilitaryEnrolled = result.data.cIsMilitaryEnrolled,
                            cIsTaxExempt = result.data.cIsTaxExempt,
                            cIsTeacherEnrolled = result.data.cIsTeacherEnrolled,
                            cLearnBrowseSocialMedia = result.data.cLearnBrowseSocialMedia,
                            cLearnLookOnline = result.data.cLearnLookOnline,
                            cLearnTakeAClass = result.data.cLearnTakeAClass,
                            cLearnVisitJoannStore = result.data.cLearnVisitJoannStore,
                            cReceiveDirectMail = result.data.cReceiveDirectMail,
                            cReceiveTextMessage = result.data.cReceiveTextMessage,
                            cRegisteredWithNarvar = result.data.cRegisteredWithNarvar,
                            cTaxExempt = result.data.cTaxExempt,
                            cInitialisationVector = result.data.cInitialisationVector,
                            cVectorKey = result.data.cVectorKey,
                            c_subscriptionPlanEndDate = result.data.c_subscriptionPlanEndDate,
                            c_subscriptionValid = result.data.c_subscriptionValid
                        )
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy { handleFetchResult(it) }
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


    fun forgotPasswordRedirection() {
        Utility.redirectToExternalBrowser(context, BuildConfig.FORGOT_PASSWORD_URL)

    }

    private fun handleFetchResult(result: Any) {
        logger.d("handleFetchResult ${result.toString()}")
        uiEvents.post(Event.OnLoginClicked)
    }

    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                errorString.set(error.message)
                uiEvents.post(Event.OnLoginFailed)
            }
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

    fun guestLogin() {
        uiEvents.post(Event.OnSeeMoreClicked)
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
        object OnSeeMoreClicked : Event()
        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object OnLandingSuccess : Event()
    }

    fun fetchViewPagerData() {

        var languageList: List<LoginViewPagerData> = listOf(
            LoginViewPagerData(
                1,
                "https://dev.w3.org/SVG/tools/svgweb/samples/svg-files/dojo.svg",
                "The Lorem Ipsum is simply dummy text of the composition and layout before printing. Lorem Ipsum has been the standard dummy text of printing since the 1500s, when an anonymous printer assembled pieces of text together to make a specimen text font book."
            ),
            LoginViewPagerData(
                2,
                "https://dev.w3.org/SVG/tools/svgweb/samples/svg-files/dojo.svg",
                "The Lorem Ipsum is simply dummy text of the composition and layout before printing. Lorem Ipsum has been the standard dummy text of printing since the 1500s, when an anonymous printer assembled pieces of text together to make a specimen text font book."
            ),
            LoginViewPagerData(
                3,
                "https://dev.w3.org/SVG/tools/svgweb/samples/svg-files/dojo.svg",
                "The Lorem Ipsum is simply dummy text of the composition and layout before printing. Lorem Ipsum has been the standard dummy text of printing since the 1500s, when an anonymous printer assembled pieces of text together to make a specimen text font book."
            ),
            LoginViewPagerData(
                4,
                "https://dev.w3.org/SVG/tools/svgweb/samples/svg-files/dojo.svg",
                "The Lorem Ipsum is simply dummy text of the composition and layout before printing. Lorem Ipsum has been the standard dummy text of printing since the 1500s, when an anonymous printer assembled pieces of text together to make a specimen text font book."
            )
        )

        viewPagerData.value = languageList
    }

    fun getLandingScreenDetails() {
        uiEvents.post(Event.OnShowProgress)
        disposable += useCase.getLandingContentDetails().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy {
                handleLandingScreenFetchDetails(it)
            }
    }

    private fun handleLandingScreenFetchDetails(it: Result<LandingContentDomain>?) {
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
                videoUrl=it.data.c_body.videoUrl
                viewPagerDataList.value=it.data.c_body.imageUrl
                uiEvents.post(Event.OnLandingSuccess)
            }
            else -> {
                uiEvents.post(Event.OnHideProgress)

            }
        }
    }
}

