package com.ditto.onboarding.ui

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.ditto.login.domain.model.LoginUser
import com.ditto.onboarding.domain.GetOnboardingData
import com.ditto.onboarding.domain.model.OnBoardingResultDomain
import com.ditto.onboarding.domain.model.OnboardingData
import com.ditto.onboarding.domain.model.OnboardingDomain
import com.ditto.storage.domain.StorageManager
import core.event.UiEvents
import core.ui.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.Error
import non_core.lib.error.NoNetworkError
import non_core.lib.whileSubscribed
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val getOnboardingData: GetOnboardingData,
    private val storageManager: StorageManager,
    private val context: Context
) : BaseViewModel() {

    var data: MutableLiveData<List<OnboardingData>> = MutableLiveData()
    var dataFromApi: MutableLiveData<List<OnboardingDomain>> = MutableLiveData()
    val clickedId: ObservableInt = ObservableInt(-1)
    val dontShowThisScreen: ObservableBoolean = ObservableBoolean(false)
    val isFromHome_Observable: ObservableBoolean = ObservableBoolean(false)
    val isBleLaterClicked: ObservableBoolean = ObservableBoolean(false)
    val isWifiLaterClicked: ObservableBoolean = ObservableBoolean(false)
    val isBluetoothOn: ObservableBoolean = ObservableBoolean(false)
    val isWifiOn: ObservableBoolean = ObservableBoolean(false)
    val onBoardingTitle: ObservableField<String> = ObservableField("")
    val onBoardingSubTitle: ObservableField<String> = ObservableField("")
    val onBoardingUserName: ObservableField<String> = ObservableField("")


    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    var errorString: ObservableField<String> = ObservableField("")
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var userId: Int = 0

    init {
        fetchDbUser()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    //fetch data from repo (via usecase)
     fun fetchOnBoardingData() {
        disposable += getOnboardingData.invoke()
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }

    }

    fun fetchOnBoardingDataFromApi() {
        disposable += getOnboardingData.invokeOnboardingContent()//Api call for content api
            .whileSubscribed { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResultFromApi(it) }
    }

    private fun fetchDbUser() {
        dbLoadError.set(false)
        disposable += getOnboardingData.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleUserResult(it) }
    }

    private fun handleFetchResult(result: Result<List<OnboardingData>>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                data.value = result.data
             if (data.value.isNullOrEmpty()){
                 handleError(NoNetworkError())
             }
                activeInternetConnection.set(true)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    private fun handleFetchResultFromApi(result: Result<OnBoardingResultDomain>) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            is Result.OnSuccess -> {
                dataFromApi.value = result.data.c_body.onboarding ?: emptyList()
                //storing data to Database
                fetchOnBoardingData()
                activeInternetConnection.set(true)
            }
            is Result.OnError -> handleError(result.error)
        }
    }

    private fun handleUserResult(result: Result<LoginUser>) {
        when (result) {
            is Result.OnSuccess<LoginUser> -> {
                isBleLaterClicked.set(result.data.bleDialogVisible ?: false)
                isWifiLaterClicked.set(result.data.wifiDialogVisible ?: false)
                uiEvents.post(Event.OnShowBleDialogue)
                Log.d("SDFasdf", result.data.wifiDialogVisible.toString())
            }
            is Result.OnError -> handleError(result.error)
        }
    }


    //error handler for data fetch related flow
    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError ->{
                errorString.set(error.message)
                activeInternetConnection.set(false)
                uiEvents.post(Event.NoNetworkError)
            }
            else -> {
                errorString.set(error.message)
                uiEvents.post(Event.DatFetchError)
                Log.d("OnboardingViewModel", "handleError")
            }
        }
    }

    fun onItemClick(id: Int) {
        clickedId.set(id)
        uiEvents.post(Event.OnItemClick)
        checkDontShowAgain(false)
    }

    fun onClickDoNotShowItAgain(toShow: Boolean) {
        dontShowThisScreen.set(toShow)
        clickLater()
    }

    fun onClickSkipAndContinue() {
        checkDontShowAgain(true)
    }


    fun onClickLater() {
        clickLater()
    }

    private fun clickLater() {
        disposable += getOnboardingData.updateDontShowThisScreen(
            0,
            dontShowThisScreen.get(),
            isBleLaterClicked.get(),
            isWifiLaterClicked.get()
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { it }
    }

    private fun checkDontShowAgain(isSkip: Boolean) {
        disposable += getOnboardingData.updateDontShowThisScreen(
            0,
            dontShowThisScreen.get(),
            isBleLaterClicked.get(),
            isWifiLaterClicked.get()
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleClick(isSkip) }
    }

    private fun handleClick(result: Boolean) {
        if (result) {
            uiEvents.post(Event.OnClickSkipAndContinue)
        } else {
            // uiEvents.post(Event.OnItemClick)
        }
    }

    /**
     * Events for this view model
     */
    sealed class Event {
        /**
         * Event emitted by [events] when the Skip & Continue is clicked
         */
        object OnClickSkipAndContinue : Event()

        /**
         * Event emitted by [events] when Do not Show It Again is clicked
         */
        object OnItemClick : Event()

        object OnShowBleDialogue : Event()

        object OnHideProgress : Event()

        object NoNetworkError : Event()

        object DatFetchError : Event()

        object OnShowProgress : Event()

    }
}
