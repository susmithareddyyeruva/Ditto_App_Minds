package com.ditto.menuitems_ui.managedevices.fragment

import android.net.nsd.NsdManager
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import core.MODE_BLE
import core.MODE_SERVICE
import core.appstate.AppState
import core.event.UiEvents
import core.models.Nsdservicedata
import core.network.NetworkUtility
import core.ui.BaseViewModel
import core.ui.common.Utility
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.InetAddress
import java.net.Socket
import javax.inject.Inject

class ManageDeviceViewModel  @Inject constructor(): BaseViewModel() {

    val isServiceNotFound : ObservableBoolean = ObservableBoolean(false)
    val isShowServiceList : ObservableBoolean = ObservableBoolean(false)
    val clickedPosition : ObservableInt = ObservableInt(0)
    val numberOfProjectors: ObservableField<String> = ObservableField("")
    val mode: ObservableField<String> = ObservableField(MODE_SERVICE)
    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    fun connectToProjector(hostAddress : String, port : Int, isShowProgress : Boolean){
        showProgress(isShowProgress)
        GlobalScope.launch {
            delay(200)
            startSocketConnection(hostAddress,port)
        }
    }

    fun sendWaitingImage(){
        showProgress(true)
        uiEvents.post(Event.OnWaitingImageSent)
    }

    fun disConnectToProjector(hostAddress : String, port : Int, isShowProgress : Boolean){
        showProgress(isShowProgress)
        GlobalScope.launch {
            delay(200)
            disconnectSocketConnection(hostAddress,port)
        }
    }

    fun Connect(){
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

    private fun showProgress(isShow : Boolean){
        if (isShow){
            uiEvents.post(Event.OnShowProgress)
        } else {
            uiEvents.post(Event.OnHideprogress)
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

     fun OnBluetoothClicked(){
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


    sealed class Event {

        object OnConnectionSuccess : Event()
        object OnConnectionFailed : Event()
        object OnShowProgress : Event()
        object OnHideprogress : Event()
        object OnSocketDisconnect : Event()
        object OnConnectClick : Event()
        object OnBleConnectClick : Event()
        object OnWaitingImageSent : Event()
        object OnConnectedImageSent : Event()
    }
}