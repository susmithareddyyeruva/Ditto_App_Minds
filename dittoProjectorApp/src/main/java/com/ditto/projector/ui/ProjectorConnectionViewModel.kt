package com.ditto.projector.ui

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.net.nsd.NsdManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.ditto.projector.R
import com.ditto.projector.ble.WifiProfile
import com.ditto.projector.common.Utility
import com.ditto.projector.common.Utility.Companion.connectToWifi
import com.ditto.projector.common.Utility.Companion.disConnectToWifi
import com.ditto.projector.common.Utility.Companion.getSharedPref
import com.ditto.projector.common.Utility.Companion.getWificred
import com.ditto.projector.core.UiEvents
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProjectorConnectionViewModel : ViewModel() {
    var disposable: CompositeDisposable = CompositeDisposable()
    var mBluetoothManager: BluetoothManager? = null
    var samplestring: ObservableField<String> = ObservableField("N/A")
    var wificonnectionstatus: ObservableField<String> = ObservableField("N/A")
    var bleconnectionstatus: ObservableField<String> = ObservableField("N/A")
    var serviceconnectionstatus: ObservableField<String> = ObservableField("N/A")
    var wifiName: ObservableField<String> = ObservableField("N/A")
    var recievedWifiName: ObservableField<String> = ObservableField("N/A")
    var alreadConnectedWifiName: ObservableField<String> = ObservableField("N/A")
    var liveconnectionstatus: ObservableField<String> = ObservableField("N/A")
    var mServiceName: ObservableField<String> = ObservableField("N/A")
    var isCallfromBle: ObservableBoolean = ObservableBoolean(false)
    var isBleConnected: ObservableBoolean = ObservableBoolean(false)
    var isConnectionFromiOS: ObservableBoolean = ObservableBoolean(false)
    var isNsdRegistered: ObservableBoolean = ObservableBoolean(false)
    var isWifiReceiverfound: ObservableBoolean = ObservableBoolean(false)
    var isWifiAlreadyConnected: ObservableBoolean = ObservableBoolean(false)
    var isAfterBleConnection: ObservableBoolean = ObservableBoolean(true)
    var mServiceRegisterPort: ObservableInt = ObservableInt(0)
    var mWifiProfile: WifiProfile? = null
      var wificredentials: String? = ""
      var decryptedssid: String? = ""
     var decryptedpwd: String? = ""
    var splitwificredentials: List<String>? = null
    var mBluetoothGattServer: BluetoothGattServer? = null
    var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    lateinit var mConnectedBLEdevice: BluetoothDevice
    val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()
    var mNsdManager: NsdManager? = null
    val SERVICE_TYPE = "_http._tcp."
    //var mServiceName = "PROJECTOR_SERVICE"
    var mRegistrationListener: NsdManager.RegistrationListener? = null

    /**
     *   [Function] Start Conenctions
     */
    fun startConnection() {
        uiEvents.post(Event.OnStartConnection)
    }

    /**
     *   [Function] Register Wifi Listener
     */
    fun registerWifiListener() {
        uiEvents.post(Event.OnRegisterListener)
    }
    /**
     *   [Function]  TEST PURPOSE : ONLY FOR SHOWING TOAST
     */
    fun startNSD() {
        uiEvents.post(Event.OnStartNSD)
    }


    /**
     * Events for this view model
     */
    sealed class Event {
        object OnRegisterListener : Event()
        object OnStartConnection : Event()
        object OnStartNSD : Event()
        object OnWaitForConnection : Event()

    }

    /**
     * [Function] Checking wifi connection to avoid reconnecting
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkCurrentWifiConnection(context: Context) {
        if (Utility.isWifiConnected(context)) {
            isWifiAlreadyConnected.set(true)
            getSharedPref(context)?.let {
                getWificred(context)?.let { it1 ->
                    disConnectToWifi(
                        it,
                        it1,
                        context
                    )
                }
            }
            GlobalScope.launch {
                delay(1000)
                connectWifiUsingCred(context)
            }
        } else {
            connectWifiUsingCred(context)
        }
        wificonnectionstatus.set(context.getString(R.string.checking))

    }

    private fun connectWifiUsingCred(context: Context) {

        decryptedssid = splitwificredentials?.get(0)?.let { Utility.decrypt(it) }
        decryptedpwd = splitwificredentials?.get(1)?.let { Utility.decrypt(it) }
        // Store wifi name in preference
        decryptedssid?.let {
            Utility.setSharedPref(
                context,
                it
            )
        }
        decryptedpwd?.let {
            Utility.setWificred(
                context,
                it
            )
        }
        recievedWifiName.set(decryptedssid)
        decryptedssid?.let {
            connectToWifi(
                it,
                decryptedpwd!!, context!!
            )
        }
        /*splitwificredentials?.get(0)?.let {
            connectToWifi(
                it,
                splitwificredentials?.get(1)!!, context!!
            )
        }*/
        uiEvents.post(Event.OnWaitForConnection)
    }


    /**
     *  [Function] Sending response to the client via BLE and starting the connection
     */
    fun sendResponseToClient(context: Context,messagetoclient: String) {
        Log.d("CONNECTIVITY_PROJECTOR", "sendResponseToClient- $messagetoclient")

        if (isBleConnected.get()) {
            liveconnectionstatus.set("Sending Response to Client..")
            val timeCharacteristic = mBluetoothGattServer?.getService(mWifiProfile?.WIFI_SERVICE)
                ?.getCharacteristic(mWifiProfile?.CREDENTIALS_CHARACTERISTICS)
            timeCharacteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            timeCharacteristic?.value = messagetoclient.toByteArray()
            Log.d("CONNECTIVITY_PROJECTOR", "mConnectedBLEdevice- $mConnectedBLEdevice : $timeCharacteristic")
            mBluetoothGattServer?.notifyCharacteristicChanged(
                mConnectedBLEdevice,
                timeCharacteristic,
                false
            )
            liveconnectionstatus.set("Response sent to Client..")
        }
        /*if (messagetoclient == context?.getString(R.string.successmessage)) {
            startConnection()
        }*/
    }
}