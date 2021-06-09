package com.ditto.menuitems_ui.settings

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.domain.LoginUser
import com.ditto.menuitems.domain.GetWorkspaceProData
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


class WSProSettingViewModel @Inject constructor(private val utility: Utility,
                                                private val context: Context,
                                                private val getWorkspaceProData: GetWorkspaceProData,
                                                val loggerFactory: LoggerFactory
) : BaseViewModel() {
    // TODO: Implement the ViewModel
    private val dbLoadError: ObservableBoolean = ObservableBoolean(false)
    private var errorString: ObservableField<String> = ObservableField("")

    private val isMirroringReminderChecked: ObservableBoolean = ObservableBoolean(false)
    private val isCutNumberChecked: ObservableBoolean = ObservableBoolean(false)
    private val isSplicingNotificationChecked: ObservableBoolean = ObservableBoolean(false)
    private val isSplicingWithMultiplePieceChecked: ObservableBoolean = ObservableBoolean(false)
    private val isClickToZoomNotification: ObservableBoolean = ObservableBoolean(false)

    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()


    init {
        if (Utility.isTokenExpired()) {
            utility.refreshToken()
        }
    }

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
                Log.d("WSProl12345", result.toString())
            }

            is Result.OnError -> {
                Log.d("WSProSettingViewModel", "Failed")
            }
        }
    }

    // need to call on switch change
    private fun updateWSProSetting(){
        disposable += getWorkspaceProData.updateWSProSetting(
            id = 1, cMirrorReminder = true, cCuttingReminder = true,
            cSpliceMultiplePieceReminder = true, cSpliceReminder = true
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { it }
    }



    fun setMirrorReminderData(value:Boolean){
        isMirroringReminderChecked.set(value)
        postBooleanDataForSettings()
    }

    fun setCutNumberSplicing(value:Boolean){
        isCutNumberChecked.set(value)
        postBooleanDataForSettings()
    }

    fun setSplicingNotification(value: Boolean){
        isSplicingNotificationChecked.set(value)
        postBooleanDataForSettings()
    }

    fun setSplicingWithMultiple(value: Boolean){
        isSplicingWithMultiplePieceChecked.set(value)
        postBooleanDataForSettings()
    }

    fun postBooleanDataForSettings(){

        //Making api call for settings
        uiEvents.post(Event.OnShowProgress)
        disposable += getWorkspaceProData.postSwitchData(
            WSSettingsInputData(
                isMirroringReminderChecked.get(),
                isCutNumberChecked.get(),
                isSplicingNotificationChecked.get(),
                isSplicingWithMultiplePieceChecked.get(),
                isClickToZoomNotification.get())
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { handleFetchResultSecond(it) }
    }


    private fun handleFetchResultSecond(result: Boolean) {
        uiEvents.post(Event.OnHideProgress)
        when (result) {
            else ->""
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

    /**
     * Events for this view model
     */
    sealed class Event {
        object isMirrorChecked : Event()
        object isCutNumberChecked : Event()
        object isSplicingNotificationChecked : Event()
        object isSplicingMultipleChecked : Event()
        object isZoomNotificationChecked : Event()

        object OnShowProgress : Event()
        object OnHideProgress : Event()
        /**
         * Event emitted by [events] when the data updated successfully
         */
        object onResponseFailed : Event()

    }
}