package com.ditto.menuitems_ui.updateprojector.fragment

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.domain.AboutAppUseCase
import com.ditto.menuitems.domain.model.AboutAppDomain
import com.ditto.menuitems_ui.managedevices.fragment.ManageDeviceViewModel
import core.MODE_BLE
import core.MODE_SERVICE
import core.appstate.AppState
import core.event.UiEvents
import core.network.NetworkUtility
import core.ui.BaseViewModel
import core.ui.common.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import non_core.lib.Result
import java.net.ConnectException
import java.net.InetAddress
import java.net.Socket
import javax.inject.Inject

//Added by vineetha for update projector popup
class UpdateProjectorViewModel @Inject constructor(private val aboutAppUseCase: AboutAppUseCase): BaseViewModel() {

    var txt:String=""
    @Inject
    lateinit var loggerFactory: LoggerFactory
    val isServiceNotFound : ObservableBoolean = ObservableBoolean(false)
    val isShowServiceList : ObservableBoolean = ObservableBoolean(false)
    var isFromBackground : Boolean = true;
    val clickedPosition : ObservableInt = ObservableInt(0)
    val numberOfProjectors: ObservableField<String> = ObservableField("")
    val mode: ObservableField<String> = ObservableField(MODE_SERVICE)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    val logger: Logger by lazy {
        loggerFactory.create(UpdateProjectorViewModel::class.java.simpleName)
    }
    fun fetchUserData() {
        disposable += aboutAppUseCase.getAboutAppAndPrivacyData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{ handleFetchResult(it) }
    }
    fun connectToProjector(hostAddress : String, port : Int, isShowProgress : Boolean){
        showProgress(isShowProgress)
        GlobalScope.launch {
            delay(1000)
            startSocketConnection(hostAddress,port)
        }
    }

    fun onBluetoothClicked(){
        mode.set(MODE_BLE)
        uiEvents.post(Event.OnBleConnectClick)
    }
    fun resetAppstate(){
        NetworkUtility.isServiceConnected = true
        NetworkUtility.nsdSericeHostName =  Utility.searchServieList?.get(clickedPosition.get())?.nsdSericeHostAddress!!
        NetworkUtility.nsdSericePortName = Utility.searchServieList?.get(clickedPosition.get())?.nsdServicePort!!
        AppState.clearSavedService()
        Utility.searchServieList?.get(clickedPosition.get())?.let {
            AppState.saveCurrentService(
                it
            )
        }
    }

    fun clearAppstate(){
        NetworkUtility.isServiceConnected = false
        NetworkUtility.nsdSericeHostName =  ""
        NetworkUtility.nsdSericePortName = 0
    }
    private fun handleFetchResult(result: Result<AboutAppDomain>?) {
        when(result)
        {
            is Result.OnSuccess<AboutAppDomain> ->{
               logger.d("AboutAppViewMode Success"+result.data)
                setResponseText(result.data.cBody)
                uiEvents.post(Event.UpdateResponseText)

            }
            is Result.OnError -> {
                logger.d("WSProSettingViewModel Failed")
            }
            else -> {}
        }
    }

    private suspend fun startSocketConnection(hostAddress : String, port : Int) {
        withContext(Dispatchers.IO) {
            val host = InetAddress.getByName(hostAddress)
            var soc: Socket? = null
            try {
                soc = Socket(host, port)
                if (soc.isConnected) {
                    NetworkUtility.nsdSericeHostName = hostAddress
                    NetworkUtility.nsdSericePortName = port
                    connectionSuccess()
                } else {
                    connectionFailed()
                }
            } catch (e: ConnectException) {
                connectionFailed()
            } catch (e: Exception) {
                connectionFailed()
            } finally {
                soc?.close()
            }
        }
    }

    private fun connectionSuccess() {
        println("******************* connectionSuccess")
        uiEvents.post(Event.OnHideprogress)
        uiEvents.post(Event.OnConnectionSuccess)
    }
    private fun connectionFailed() {
        uiEvents.post(Event.OnHideprogress)
        uiEvents.post(Event.OnConnectionFailed)
    }
    fun setResponseText(txt: String){
        this.txt=txt
    }

    fun getResponseText():String{
        return txt;
    }
    fun sendWaitingImage(){
        showProgress(true)
        uiEvents.post(Event.OnWaitingImageSent)
    }
    private fun showProgress(isShow : Boolean){
        if (isShow){
            uiEvents.post(Event.OnShowProgress)
        } else {
            uiEvents.post(Event.OnHideprogress)
        }
    }
    fun disConnectToProjector(hostAddress : String, port : Int, isShowProgress : Boolean){
        showProgress(isShowProgress)
        GlobalScope.launch {
            delay(200)
            disconnectSocketConnection(hostAddress,port)
        }
    }

    fun connect(){
        uiEvents.post(Event.OnConnectClick)
    }
    private suspend fun disconnectSocketConnection(hostAddress : String, port : Int) {

        withContext(Dispatchers.IO) {
            val host = InetAddress.getByName(hostAddress)
            var soc: Socket? = null
            try {
                soc = Socket(host, port)
                soc.close()
                NetworkUtility.nsdSericeHostName = ""
                NetworkUtility.nsdSericePortName = 0
                uiEvents.post(Event.OnSocketDisconnect)
            } catch (e: ConnectException) {
                uiEvents.post(Event.OnSocketDisconnect)
            } catch (e: Exception) {
                uiEvents.post(Event.OnSocketDisconnect)
            } finally {
                soc?.close()
            }
        }
    }

    sealed class Event {
        object UpdateResponseText : Event()
        object OnShowProgress : Event()
        object OnBleConnectClick : Event()
        object OnWaitingImageSent : Event()
        object OnHideprogress : Event()
        object OnConnectClick : Event()
        object OnSocketDisconnect : Event()
        object OnConnectionSuccess : Event()
        object OnConnectionFailed : Event()
        object OnConnectedImageSent : Event()
    }


}