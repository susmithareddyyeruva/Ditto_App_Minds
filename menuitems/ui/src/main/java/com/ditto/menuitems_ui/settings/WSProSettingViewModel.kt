package com.ditto.menuitems_ui.settings

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.domain.LoginUser
import com.ditto.login.domain.model.LoginViewPagerData
import com.ditto.menuitems.domain.GetWorkspaceProData
import com.ditto.menuitems.domain.model.LoginResult
import com.ditto.menuitems.domain.model.WSSettingsInputData
import core.event.UiEvents
import core.ui.BaseViewModel
import core.ui.common.Utility
import io.reactivex.Single
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

     val isMirroringReminderChecked: ObservableBoolean = ObservableBoolean(false)
     val isCutNumberChecked: ObservableBoolean = ObservableBoolean(false)
     val isSplicingNotificationChecked: ObservableBoolean = ObservableBoolean(false)
     val isSplicingWithMultiplePieceChecked: ObservableBoolean = ObservableBoolean(false)
     val isClickToZoomNotification: ObservableBoolean = ObservableBoolean(false)

    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    var userData: MutableLiveData<LoginUser> = MutableLiveData()

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
                userData.value = result.data
                resetData()
            }
            is Result.OnError -> {
                Log.d("WSProSettingViewModel", "Failed")
            }
        }
    }

    // need to call on switch change
    private fun updateWSProSetting(){
        disposable += getWorkspaceProData.updateWSProSetting(
            id = 1, cMirrorReminder =  isMirroringReminderChecked.get(),
            cCuttingReminder =  isCutNumberChecked.get(),
            cSpliceReminder =  isSplicingNotificationChecked.get(),
            cSpliceMultiplePieceReminder =  isSplicingWithMultiplePieceChecked.get()

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


    private fun handleFetchResultSecond(result: Result<LoginResult>) {
        uiEvents.post(Event.OnHideProgress)
        updateWSProSetting()
    }

    private fun onFetchComplete(){
        uiEvents.post(Event.OnFetchComplete)
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
   /*     object isMirrorChecked : Event()
        object isCutNumberChecked : Event()
        object isSplicingNotificationChecked : Event()
        object isSplicingMultipleChecked : Event()
        object isZoomNotificationChecked : Event()*/

        object OnShowProgress : Event()
        object OnHideProgress : Event()
        object OnFetchComplete : Event()


    }

    private fun resetData(){
        isMirroringReminderChecked.set(userData.value?.cMirrorReminder!!)
        isCutNumberChecked.set(userData.value?.cCuttingReminder!!)
         isSplicingNotificationChecked.set(userData.value?.cSpliceCutCompleteReminder!!)
         isSplicingWithMultiplePieceChecked.set(userData.value?.cSpliceMultiplePieceReminder!!)
        onFetchComplete()
    }
}