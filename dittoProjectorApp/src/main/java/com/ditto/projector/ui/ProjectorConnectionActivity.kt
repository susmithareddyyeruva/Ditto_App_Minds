package com.ditto.projector.ui

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.ContentValues
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.ditto.projector.R
import com.ditto.projector.ble.WifiProfile
import com.ditto.projector.common.Utility
import com.ditto.projector.common.WifiConnectionListener
import com.ditto.projector.databinding.ActivityProjectorConnectionBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_projector_connection.*
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.system.exitProcess

class ProjectorConnectionActivity : AppCompatActivity(),
    WifiConnectionListener.ConnectivityReceiverListener {

    private val viewModel: ProjectorConnectionViewModel by lazy {
        ViewModelProviders.of(this).get(ProjectorConnectionViewModel::class.java)
    }
    private lateinit var mConnectionServerSocket: ServerSocket
    private lateinit var mConnectionSocket: Socket
    private lateinit var imgaeReceivingJob: Job
    var wifiConnectionWaitingJob: Job? = null
    private lateinit var imageBitMap: Bitmap
    private var wifiReceiver: WifiConnectionListener? = null
    var deviceid : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityProjectorConnectionBinding>(
            this,
            R.layout.activity_projector_connection
        )
            .apply {
                projectorViewModel = viewModel; lifecycleOwner = this@ProjectorConnectionActivity
            }
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
        img_receivedimage.setImageResource(R.drawable.setup_pattern_waiting)
        startBLE()
        initapp()
//        deviceid= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
        deviceid = BluetoothAdapter.getDefaultAdapter().name
        title_proj.text = "Ditto Projector " + "( ID : " + deviceid + " )"
    }

    /**
     * BLE  [Function] Starting BLE GATT Server
     */
    private fun startBLE() {
        viewModel.isBleConnected.set(false)
        viewModel.mBluetoothManager =
            this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        viewModel.mBluetoothGattServer =
            viewModel.mBluetoothManager?.openGattServer(this, mGattServerCallback)
        if (viewModel.mBluetoothGattServer == null) {
            Log.w(ContentValues.TAG, "Unable to create GATT server")
            return
        }
        viewModel.mWifiProfile = WifiProfile()
        viewModel.mBluetoothGattServer?.addService(viewModel.mWifiProfile?.buildWifiService())
        startAdvertising()
    }

    /**
     * BLE  [Function] Start advertising the BLE service
     */
    private fun startAdvertising() {
        val bluetoothAdapter = viewModel.mBluetoothManager?.adapter
        viewModel.mBluetoothLeAdvertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        if (viewModel.mBluetoothLeAdvertiser == null) {
            Log.w(ContentValues.TAG, "Failed to create advertiser")
            return
        }
        val settings =
            AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(false)
            .addServiceUuid(ParcelUuid(viewModel.mWifiProfile?.CREDENTIALS_CHARACTERISTICS))
            .build()
        viewModel.mBluetoothLeAdvertiser?.startAdvertising(settings, data, mAdvertiseCallback)

    }

    /**
     * BLE  [Function] callback after advertising started
     */
    private val mAdvertiseCallback: AdvertiseCallback =
        object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                viewModel.bleconnectionstatus.set(getString(R.string.discovery_started))
            }

            override fun onStartFailure(errorCode: Int) {
                viewModel.bleconnectionstatus.set(getString(R.string.discovery_failed))
            }
        }

    /**
     * BLE  [Function] GATT server callbacks
     */
    private val mGattServerCallback: BluetoothGattServerCallback =
        object : BluetoothGattServerCallback() {
            override fun onConnectionStateChange(
                device: BluetoothDevice,
                status: Int,
                newState: Int
            ) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    viewModel.mConnectedBLEdevice = device
                    viewModel.isBleConnected.set(true)
                    viewModel.bleconnectionstatus.set("Waiting for handshake!!")
                    viewModel.isAfterBleConnection.set(false)
                    registerListener()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    viewModel.isBleConnected.set(false)
                    if (viewModel.isConnectionFromiOS.get()) {
                        viewModel.bleconnectionstatus.set(getString(R.string.connected))
                    } else {
                        viewModel.bleconnectionstatus.set(getString(R.string.disconnect))
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onCharacteristicWriteRequest(
                device: BluetoothDevice,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray
            ) {
                viewModel.samplestring.set("BLE request received")
                viewModel.isAfterBleConnection.set(true)
                showToast()
                viewModel.wificredentials = String(value)
                if (viewModel.wificredentials!!.startsWith(getString(R.string.BLEconnectionrequest))) {
                    viewModel.bleconnectionstatus.set(getString(R.string.connected))
                    Log.d(
                        "CONNECTIVITY_PROJECTOR",
                        "onCharacteristicWriteRequest() - TRACEbleConnected"
                    )
                    getString(R.string.BLEconnected)?.let {
                        viewModel.sendResponseToClient(
                            applicationContext,
                            it
                        )
                    }
                    if (viewModel.wificredentials!!.equals(getString(R.string.BLEconnectionrequestIOS))) {
                        viewModel.isConnectionFromiOS.set(true)
                    } else {
                        viewModel.isConnectionFromiOS.set(false)
                    }
                } else {
                    try {
                        viewModel.liveconnectionstatus.set(getString(R.string.cred_received))
                        viewModel.splitwificredentials = viewModel.wificredentials!!.split(",")
                        //viewModel.samplestring.set("Received Credentials " + viewModel.splitwificredentials)
                        viewModel.samplestring.set("Received Wifi Credentials ")
                        //------ For testing Pupose (showing the decrypted value)----------//
                        /*viewModel.samplestring.set("Received Credentials " + viewModel.splitwificredentials?.get(0)?.let { Utility.decrypt(it) }
                        + ", "+viewModel.splitwificredentials?.get(1)?.let { Utility.decrypt(it) }+ ", "
                        + viewModel.splitwificredentials?.get(2)?.let { Utility.decrypt(it) }
                        )*/
                        if (viewModel.splitwificredentials!![2] == "IOS") {
                            viewModel.bleconnectionstatus.set(getString(R.string.connected))
                        }
                        /*// Store wifi name in preference
                        Utility.setSharedPref(
                            this@ProjectorConnectionActivity,
                            viewModel.splitwificredentials!![0]
                        )*/
                        showToast()
                        //viewModel.wifiName.set(viewModel.splitwificredentials!![0])
                        viewModel.isWifiReceiverfound.set(false)
                        viewModel.checkCurrentWifiConnection(applicationContext)
                    } catch (e: java.lang.Exception) {
                        Log.d("CONNECTIVITY_PROJECTOR", "onCharacteristicWriteRequest() -$e")
                        viewModel.liveconnectionstatus.set("Improper Request Received..")
                        getString(R.string.failedrequest)?.let {
                            viewModel.sendResponseToClient(
                                applicationContext,
                                it
                            )
                        }
                    }
                }

            }

            override fun onDescriptorReadRequest(
                device: BluetoothDevice?,
                requestId: Int,
                offset: Int,
                descriptor: BluetoothGattDescriptor?
            ) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor)

            }

            override fun onDescriptorWriteRequest(
                device: BluetoothDevice?,
                requestId: Int,
                descriptor: BluetoothGattDescriptor?,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray?
            ) {
                super.onDescriptorWriteRequest(
                    device,
                    requestId,
                    descriptor,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value
                )

                if (responseNeeded) {
                    viewModel.mBluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        value
                    );

                }

            }
        }


    /**
     * [Function] Registering Service / Starting BLE based on Wifi connection Status
     */
    private fun initapp() {
        viewModel.isNsdRegistered.set(false)
        if (Utility.isWifiConnected(this)) {
            viewModel.wificonnectionstatus.set(getString(R.string.connected))
            viewModel.isCallfromBle.set(false)
            //startNSD()
            getWIFIname()
            viewModel.alreadConnectedWifiName.set(viewModel.wifiName.get())
            //viewModel.wifiName.set(Utility.getSharedPref(this))
        } else {
            viewModel.wificonnectionstatus.set(getString(R.string.NotConnected))
        }
    }

    /**
     * WIFI  [Function] Initializing NsdHelper class
     */
    private fun startNSD() {
        try {
            Log.d("CONNECTIVITY_PROJECTOR", "startNSD()")
            viewModel.mNsdManager =
                this.getSystemService(Context.NSD_SERVICE) as NsdManager
            viewModel.mServiceRegisterPort.set(Utility.findFreePort())
            registerService(viewModel.mServiceRegisterPort.get())

        } catch (e: Exception) {
            Log.d("CONNECTIVITY_PROJECTOR", "startNSD()  Exception -$e")
            viewModel.sendResponseToClient(this, e.toString())
        }

    }

    fun registerService(port: Int) {
        tearDown("FROM REGISTERING SERVICE")
        initializeRegistrationListener()
        val serviceInfo = NsdServiceInfo()
        serviceInfo.port = port
        //viewModel.mServiceName.set("DITTO_"+viewModel.mBluetoothManager!!.adapter.address)
//        viewModel.mServiceName.set("DITTO_" + deviceid)
        if(deviceid.isNullOrEmpty()){
            deviceid = Settings.System.getString(getContentResolver(), "device_name")
        }
        viewModel.mServiceName.set(deviceid)
        serviceInfo.serviceName = viewModel.mServiceName.get()
        serviceInfo.serviceType = viewModel.SERVICE_TYPE
        Log.d("CONNECTIVITY_PROJECTOR", "register Service- $serviceInfo")
        viewModel.mNsdManager = (this.getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(
                serviceInfo,
                NsdManager.PROTOCOL_DNS_SD,
                viewModel.mRegistrationListener
            )
        }

    }


    private fun initializeRegistrationListener() {
        viewModel.mRegistrationListener = object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
                Log.d("CONNECTIVITY_PROJECTOR", "onServiceRegistered- $nsdServiceInfo")
                viewModel.mServiceName.set(nsdServiceInfo.serviceName)
                onNsdServiceRegistered(viewModel.mServiceName.get()!!)
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.d("CONNECTIVITY_PROJECTOR", "onRegistrationFailed- $serviceInfo")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                Log.d("CONNECTIVITY_PROJECTOR", "onServiceUnregistered- $arg0")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.d("CONNECTIVITY_PROJECTOR", "onUnregistrationFailed- $serviceInfo")
            }
        }
    }


    private fun handleEvent(event: ProjectorConnectionViewModel.Event) =
        when (event) {
            is ProjectorConnectionViewModel.Event.OnRegisterListener -> {
                registerListener()
            }
            is ProjectorConnectionViewModel.Event.OnStartConnection -> {
                startConnectionServer()
            }
            is ProjectorConnectionViewModel.Event.OnStartNSD -> {
                startNSD()
            }
            is ProjectorConnectionViewModel.Event.OnWaitForConnection -> {
                startWaiting()
            }
        }

    fun startWaiting() {
        wifiConnectionWaitingJob = GlobalScope.launch {
            delay(20000)
            viewModel.wificonnectionstatus.set("Not Connected")
            Log.d("CONNECTIVITY_PROJECTOR", "startWaiting() - TRACEFailedWifi")
            viewModel.sendResponseToClient(applicationContext, "TRACEFailedWifi")
        }
    }

    fun showToast() {
        /* this@ProjectorConnectionActivity.runOnUiThread(java.lang.Runnable {
             Toast.makeText(this, viewModel.samplestring.get(), Toast.LENGTH_SHORT).show()
         })*/
    }

    /**
     * [Function] Registering Listener for Wifi Connection callback
     */
    private fun registerListener() {
        viewModel.isWifiReceiverfound.set(false)
        wifiReceiver = WifiConnectionListener()
        Log.d("CONNECTIVITY_PROJECTOR", "@@@@ registerListener  : " + wifiReceiver)
        registerReceiver(
            wifiReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean, wifiName: String?) {
        if (isConnected && !viewModel.isWifiReceiverfound.get() && viewModel.isAfterBleConnection.get()) {
            wifiConnectionWaitingJob?.cancel()
            viewModel.isWifiReceiverfound.set(true)
            viewModel.wificonnectionstatus.set(getString(R.string.connected))
            wifiName?.let { viewModel.wifiName.set(wifiName) }
            viewModel.isCallfromBle.set(true)
            viewModel.startNSD()
        }
    }


    fun onNsdServiceRegistered(mservicename: String) {
        Log.d("CONNECTIVITY_PROJECTOR", "onNsdServiceRegistered- $mservicename")
        viewModel.mServiceName.set(mservicename)
        viewModel.isNsdRegistered.set(true)
        viewModel.serviceconnectionstatus.set(mservicename + " Registered Successfully")
        viewModel.startConnection()
        GlobalScope.launch {
            delay(3000)
            sendSuccessToClient()
        }

        /*if (viewModel.isCallfromBle.get()) {
            viewModel.sendResponseToClient(this,getString(R.string.successmessage))
        } else {
            startConnectionServer()
        }*/
    }

    private fun sendSuccessToClient() {
        viewModel.samplestring.set("Sending Response to client")
        showToast()
        viewModel.sendResponseToClient(
            this,
            getString(R.string.successmessage) + "," + viewModel.mServiceName.get()
        )
    }


    /**
     *  [Function] To make a connection using sockets
     */
    private fun startConnectionServer() {
        GlobalScope.launch {
            delay(2000)
            viewModel.liveconnectionstatus.set("Waiting for Connection...")
            viewModel.mServiceRegisterPort.get().let { startServerConnection(it) }
        }
    }

    /**
     *  [Function] Coroutine function to connect the client using sockets
     */
    private suspend fun startServerConnection(mServerPort: Int) {
        withContext(Dispatchers.IO) {
            while (true) {
                try {
                    Log.d("CONNECTIVITY_PROJECTOR", "**** Socket")
                    mConnectionServerSocket = ServerSocket();
                    mConnectionServerSocket.reuseAddress = true;
                    mConnectionServerSocket.bind(InetSocketAddress(mServerPort));
                    //mConnectionSocket = mConnectionServerSocket.accept()
                    //viewModel.liveconnectionstatus.set("Connected to Client(Android or IOS)")
                    startImageReceivingConnection()
                } catch (e: Exception) {
                    Log.d("CONNECTIVITY_PROJECTOR", "startServerConnection - $e")
                    viewModel.liveconnectionstatus.set("Connection Failed")
                    viewModel.sendResponseToClient(applicationContext, "TRACEFailed")
                }
            }
        }
    }

    /**
     *  [Function] Coroutine function to receive images from client using sockets
     */
    private suspend fun startImageReceivingConnection() {
        withContext(Dispatchers.IO) {
            while (true) {
                Log.d("CONNECTIVITY_PROJECTOR", "@@@@ Socket")
                mConnectionSocket = mConnectionServerSocket.accept()
                imgaeReceivingJob = launch(Dispatchers.IO) {
                    try {
                        val datainput: DataInputStream =
                            DataInputStream(mConnectionSocket.getInputStream())
                        if (datainput != null) {
                            val imageBytes: ByteArray = datainput.readBytes()
                            //viewModel.samplestring.set("Recevied bytes " + imageBytes.size)
                            viewModel.liveconnectionstatus.set("Connected to Client(Android or IOS)")
                            println("TRACE_APP_CONNECTIONS   *******  : " + mConnectionSocket.inetAddress)
                            withContext(Dispatchers.Main) {
                                viewModel.samplestring.set("Receving Request from Socket")
                                showToast()
                                println(
                                    "TRACE_APP_CONNECTIONS   imageBytes *******  : " + imageBytes.size +
                                            " : " + mConnectionSocket.getInputStream().read()
                                )
                                if (imageBytes.isNotEmpty()) {
                                    println("TRACE_APP_CONNECTIONS  imageBytes not null")
                                    showImage(imageBytes)
                                }
                            }

                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        mConnectionSocket.close()
                    }
                }
            }
        }
    }

    /**
     *  [Function] Converting bytearray received from client to imageview
     */
    private fun showImage(imagebytes: ByteArray) {
        try {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            imageBitMap = BitmapFactory.decodeByteArray(imagebytes, 0, imagebytes.size, options)
            img_receivedimage.setImageBitmap(imageBitMap)
            status_lay.visibility = View.GONE
            image_lay.visibility = View.VISIBLE
            Log.d(
                "TRACE_ Projection :",
                " TRACE_ Projection showImage " + Calendar.getInstance().timeInMillis
            )
        } catch (e: java.lang.Exception) {
            println("TRACE_APP_CONNECTIONS   Cannot convert to image *******  : " + e)
            viewModel.samplestring.set("Cannot convert to image - Exception - $e")
            showToast()
        }
    }

    //----------------------------------Different approach for two platforms------------------------//
    /**
     *  [Function] Coroutine function to connect the client using sockets
     */
    /*private fun startServerConnection(mServerPort: Int) {
        runBlocking {
            while (true) {
                try {
                    mConnectionServerSocket = ServerSocket(mServerPort)
                    mConnectionSocket = mConnectionServerSocket.accept()
                    var datainput: DataInputStream =
                        DataInputStream(mConnectionSocket.getInputStream())
                    if (datainput != null) {
                        val clientrequest = String(datainput.readBytes())
                        if (clientrequest.equals("TRACEIOS")) {
                            viewModel.liveconnectionstatus.set("Connected to IOS Client!!")
                            startImageReceivingConnection_IOS()
                        } else {
                            viewModel.liveconnectionstatus.set("Connected to Android Client!!")
                            startImageReceivingConnection()

                        }
                    } else {
                        viewModel.liveconnectionstatus.set("Connected to unknown!!")
                        startImageReceivingConnection()
                    }
                } catch (e: Exception) {
                    viewModel.liveconnectionstatus.set("Connection Failed")
                    viewModel.sendResponseToClient(applicationContext,"TRACEFailed")
                }
             }
        }
    }*/

    /**
     *  [Function] Coroutine function to receive images from client using sockets
     */
    /* private fun startImageReceivingConnection() {
         runBlocking {
             while (true) {
                 mConnectionSocket = mConnectionServerSocket.accept()
                 imgaeReceivingJob = launch(Dispatchers.Default) {
                     try {
                         val imageObjectInputStream =
                             ObjectInputStream(mConnectionSocket.getInputStream())
                         val imageObject: Any = imageObjectInputStream.readObject()
                         val imageBytes: ByteArray = imageObject as ByteArray
                         Log.d("TRACE", "Received Image Size " + imageBytes.size)
                         if (imageBytes.isNotEmpty()) {
                             showImage(imageBytes)
                         }
                     } catch (e: Throwable) {
                         e.printStackTrace()
                          mConnectionSocket.close()
                     }
                 }
             }
         }
     }*/

    /**
     *  [Function] Coroutine function to receive images from client using sockets
     */
    /*private fun startImageReceivingConnection_IOS() {
        runBlocking {
            while (true) {
                mConnectionSocket = mConnectionServerSocket.accept()
                imgaeReceivingJob = launch(Dispatchers.Default) {
                    try {
                        val inputStream = mConnectionSocket.getInputStream()
                        val baos = ByteArrayOutputStream()
                        var reads = inputStream.read()
                        while (reads != -1)
                        {
                            baos.write(reads)
                            reads = inputStream.read()
                        }
                        var bufferbytes: ByteArray? = null
                        bufferbytes = baos.toByteArray()
                        if (bufferbytes?.isNotEmpty()!!) {
                            showImage(bufferbytes)
                        }

                    } catch (e: Throwable) {
                        e.printStackTrace()
                        mConnectionSocket.close()
                    }
                }
            }

        }
    }*/

    //---------------------------------------------------------------------------------------------//

    private fun tearDown(place: String) {
        Log.d("CONNECTIVITY_PROJECTOR", "teardown entered")
        if (::mConnectionSocket.isInitialized && mConnectionSocket.isConnected) {
            Log.d("CONNECTIVITY_PROJECTOR", "teardown - mConnectionSocket.isConnected")
            mConnectionSocket.close()
        }
        if (viewModel.mRegistrationListener != null) {
            try {
                Log.d(
                    "CONNECTIVITY_PROJECTOR",
                    "teardown - viewModel.mRegistrationListener != null"
                )
                viewModel.mNsdManager?.unregisterService(viewModel.mRegistrationListener)
            } finally {
                Log.d("teardown", "final block")
            }
            viewModel.mRegistrationListener = null
        }
    }

    private fun unRegisterWifiListener() {
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver)
            wifiReceiver = null
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onPause() {
        tearDown("FROM ONPAUSE")
        unRegisterWifiListener()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerWifiListener()
        WifiConnectionListener.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        tearDown("FROM ONDESTROY")
        unRegisterWifiListener()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        exitProcess(-1)
    }

    fun getWIFIname() {
        try {
            val wifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo
            wifiInfo = wifiManager.connectionInfo
            if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                viewModel.wifiName.set(wifiInfo.ssid.replace("\"", ""))
                if (viewModel.wifiName.get()!!.toLowerCase()!!.contains("unknown")) {
                    viewModel.wifiName.set("N/A")
                }
            }
            val wifi: WifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val networkList: List<ScanResult> = wifi.scanResults
            if (networkList != null) {
                for (network in networkList) {
                    if (viewModel.wifiName.get().equals(network.SSID)) {

                    }
                }
            }

        } catch (e: Exception) {
            Log.d("exception", "wifi")
        }
    }
}