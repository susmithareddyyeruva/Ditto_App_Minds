package com.ditto.menuitems_ui.settings

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.domain.model.LoginUser
import com.ditto.menuitems.domain.GetWorkspaceProData
import com.ditto.menuitems.domain.model.WSProSettingDomain
import com.ditto.menuitems.domain.model.WSSettingsInputData
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


class WSProSettingViewModel @Inject constructor(
    private val utility: Utility,
    private val context: Context,
    private val getWorkspaceProData: GetWorkspaceProData,
    val loggerFactory: LoggerFactory,
) : BaseViewModel() {
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    var errorString: ObservableField<String> = ObservableField("")

    val isMirroringReminderChecked: ObservableBoolean = ObservableBoolean(false)
    val isCutNumberChecked: ObservableBoolean = ObservableBoolean(false)
    val isSplicingNotificationChecked: ObservableBoolean = ObservableBoolean(false)
    val isSplicingWithMultiplePieceChecked: ObservableBoolean = ObservableBoolean(false)
    val isClickToZoomNotification: ObservableBoolean = ObservableBoolean(false)
    val isSaveCalibrationPhotos: ObservableBoolean = ObservableBoolean(false)
    val isFromErrorPopUp: ObservableBoolean =
        ObservableBoolean(false)// used for handling error scenario

    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    var userData: MutableLiveData<LoginUser> = MutableLiveData()


    val logger: Logger by lazy {
        loggerFactory.create(WSProSettingViewModel::class.java.simpleName)
    }

    fun fetchUserData() {
        disposable += getWorkspaceProData.getUserDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResult(it) }
    }

    private fun handleFetchResult(result: Result<LoginUser>?) {
        when (result) {
            is Result.OnSuccess<LoginUser> -> {
                userData.value = result.data
                resetData()
            }
            is Result.OnError -> {
                logger.d("WSProSettingViewModel, Failed")
            }
            else -> {}
        }
    }

    // need to call on switch change
    private fun updateWSProSetting() {
        disposable += getWorkspaceProData.updateWSProSetting(
            id = 1, cMirrorReminder = isMirroringReminderChecked.get(),
            cCuttingReminder = isCutNumberChecked.get(),
            cSpliceReminder = isSplicingNotificationChecked.get(),
            cSpliceMultiplePieceReminder = isSplicingWithMultiplePieceChecked.get(),
            cSaveCalibrationPhotos = isSaveCalibrationPhotos.get()
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { it }
    }


    fun setMirrorReminderData(value: Boolean) {
        isMirroringReminderChecked.set(value)
        postBooleanDataForSettings()
    }

    fun setCutNumberSplicing(value: Boolean) {
        isCutNumberChecked.set(value)
        postBooleanDataForSettings()
    }

    fun setSplicingNotification(value: Boolean) {
        isSplicingNotificationChecked.set(value)
        postBooleanDataForSettings()
    }

    fun setSplicingWithMultiple(value: Boolean) {
        isSplicingWithMultiplePieceChecked.set(value)
        postBooleanDataForSettings()
    }

    fun setSaveCalibrationImage(value: Boolean) {
        isSaveCalibrationPhotos.set(value)
        postBooleanDataForSettings()
    }

    fun postBooleanDataForSettings() {

        //Making api call for settings
        uiEvents.post(Event.OnShowProgress)
        disposable += getWorkspaceProData.postSwitchData(
            WSSettingsInputData(
                isMirroringReminderChecked.get(),
                isCutNumberChecked.get(),
                isSplicingNotificationChecked.get(),
                isSplicingWithMultiplePieceChecked.get(),
                isSaveCalibrationPhotos.get(),
                isClickToZoomNotification.get())
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResultSecond(it) }
    }


    private fun handleFetchResultSecond(result: Result<WSProSettingDomain>) {
        uiEvents.post(Event.OnHideProgress)
        //updateWSProSetting()
        when (result) {
            is Result.OnSuccess -> {
                logger.d("handleFetchResultSecond >>>Success>>>> " + result.data)
            }
            is Result.OnError -> {
                logger.d("Failed")
                handleError(result.error)
            }
        }
    }

    private fun handleError(error: Error) {
        when (error) {
            is NoNetworkError -> {
                activeInternetConnection.set(false)
                errorString.set(error.message)
                uiEvents.post(Event.NoInternet)
            }

            else -> {
                errorString.set(error.message)
                uiEvents.post(Event.OnResultFailed)
            }

        }
    }

    private fun onFetchComplete() {
        uiEvents.post(Event.OnFetchComplete)
    }


    /**
     * Events for this view model
     */
    sealed class Event {
        /*     object isMirrorChecked : Event()
             object isCutNumberChecked : Event()
             object isSplicingNotificationChecked : Event()
             object isSplicingMultipleChecked : Event()
             object isZoomNotificationChecked : Event()*/

        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object OnFetchComplete : Event()
        object NoInternet : Event()
        object OnResultFailed : Event()


    }

    private fun resetData() {
        setToggleButtonValue()
        onFetchComplete()
    }

    fun setToggleButtonValue() {
        isMirroringReminderChecked.set(userData.value?.cMirrorReminder!!)
        isCutNumberChecked.set(userData.value?.cCuttingReminder!!)
        isSplicingNotificationChecked.set(userData.value?.cSpliceReminder!!)
        isSplicingWithMultiplePieceChecked.set(userData.value?.cSpliceMultiplePieceReminder!!)
        isSaveCalibrationPhotos.set(userData.value?.cSaveCalibrationPhotos!!)
    }
}