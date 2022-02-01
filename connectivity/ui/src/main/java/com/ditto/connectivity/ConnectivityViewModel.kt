package com.ditto.connectivity


import android.os.StrictMode
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import core.event.UiEvents
import core.ui.BaseViewModel
import javax.inject.Inject


class ConnectivityViewModel @Inject constructor() : BaseViewModel() {

    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    val isProjectorLayout : ObservableBoolean = ObservableBoolean(false)
    val isWifiError : ObservableBoolean = ObservableBoolean(false)
    val isServiceError : ObservableBoolean = ObservableBoolean(false)
    val alerMessage: ObservableField<String> = ObservableField("")
    val isErrorLayout : ObservableBoolean = ObservableBoolean(false)
    val isWifiCredLayout : ObservableBoolean = ObservableBoolean(false)
    val isDeviceListLayout : ObservableBoolean = ObservableBoolean(false)
    val isProgressBar : ObservableBoolean = ObservableBoolean(false)
    val isLocationEnabled : ObservableBoolean = ObservableBoolean(false)
    val isNoServiceFound : ObservableBoolean = ObservableBoolean(false)
    val isNoBleFound : ObservableBoolean = ObservableBoolean(false)
    val isShowServiceList : ObservableBoolean = ObservableBoolean(false)
    val isServiceFoundAfterWifi : ObservableBoolean = ObservableBoolean(false)
    val isShowManageDeviceLay : ObservableBoolean = ObservableBoolean(false)
    var isBLEConnected:Boolean = false


    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(ConnectivityViewModel::class.java.simpleName)
    }

    fun onClickCancel() {
        uiEvents.post(Event.OnCancelClicked)
    }

    fun onClickCancelCredentials() {
        uiEvents.post(Event.OnCancelCredentialClicked)
    }

    fun onClickCancelError() {
        uiEvents.post(Event.OnCancelErrorClicked)
    }

    fun onClickSkip() {
        uiEvents.post(Event.OnSkipClicked)
    }

    fun onClickSkipCredentials() {
        uiEvents.post(Event.OnSkipCredentialsClicked)
    }

    fun onClickConnect() {
        uiEvents.post(Event.OnConnectClicked)
    }

    fun onClickRetry() {
        uiEvents.post(Event.OnRetryClicked)
    }

    fun onClickRefresh() {
        uiEvents.post(Event.OnRefreshClicked)
    }
    fun onClickProjRefresh() {
        uiEvents.post(Event.OnProjRefreshClicked)
    }
    fun onClickProjscanViaBle() {
        uiEvents.post(Event.OnProjScanViaBleClicked)
    }

    /**
     * Events for this view model
     */
    sealed class Event {
        object OnCancelClicked : Event()
        object OnCancelCredentialClicked : Event()
        object OnCancelErrorClicked : Event()
        object OnSkipClicked : Event()
        object OnSkipCredentialsClicked : Event()
        object OnConnectClicked : Event()
        object OnRetryClicked : Event()
        object OnRefreshClicked : Event()
        object OnProjRefreshClicked : Event()
        object OnProjScanViaBleClicked : Event()
    }

     fun setThreadPolicy(){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Log.d(ConnectivityUtils.TAG,"Set Thread Policy")
    }
}