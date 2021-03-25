package com.ditto.connectivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.location.LocationManager
import android.net.Uri
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.ditto.connectivity.databinding.ConnectivityActivityBinding
import com.ditto.connectivity.service.BluetoothLeService
import com.ditto.connectivity.R
import core.network.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.connectivity_activity.*
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.InetAddress
import java.net.Socket

class ConnectivityActivity : AppCompatActivity() {

    private var mLeDeviceListAdapter: LeDeviceListAdapter? = null
    private var mHandler: Handler? = null
    private var mScanning: Boolean = false
    private val SCAN_PERIOD: Long = 10000
    private var mDeviceAddress: String? = null
    private var mDeviceName: String? = null
    private var connSSID: String = ""
    private var networkType: String = ""
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val GPS_REQUEST = 1001

    //BLE SERVICE
    private var mBluetoothLeService: BluetoothLeService? = null

    private val mGattCharacteristics: MutableCollection<BluetoothGattCharacteristic> = ArrayList()

    val viewModel: ConnectivityViewModel by viewModels()
    lateinit var binding: ConnectivityActivityBinding

    var nsdManager: NsdManager? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var resolveListener: NsdManager.ResolveListener? = null
    private var mService: NsdServiceInfo? = null
    private var isServiceFound: Boolean = false
    private var nsdservice: NsdServiceInfo? = null
    private var wifiConnectionWaitingJob: Job? = null
    private var bleConnectionWaitingJob: Job? = null
    private var serviceConnectionWaitingJob: Job? = null
    private var isBinded = false

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(ConnectivityUtils.TAG, "Activity goes to OnCreate ")
        val binding: ConnectivityActivityBinding =
            DataBindingUtil.setContentView(this,
                R.layout.connectivity_activity
            )
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        setUIEvents()
        setupKeyboardListener(binding.root)
        viewModel.setThreadPolicy()
        initNSD()
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        Log.d(ConnectivityUtils.TAG, "BLE Adapter Initialized")

        mHandler = Handler()
        getWIFIname()
        wifiname.setText(connSSID)
        viewModel.isProjectorLayout.set(true)
        viewModel.isErrorLayout.set(false)
        viewModel.isWifiCredLayout.set(false)

       /* window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)*/
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
            val actionBar: ActionBar? = actionBar
            if (actionBar != null) {
                actionBar.hide()
            }
        }

        turnGPSOn(object :
            onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                if(isGPSEnable) {
                   initWIFIService()
                }
            }
        })




        deviceList!!.setOnItemClickListener { parent, view, position, id ->
            Log.d(ConnectivityUtils.TAG, "Device list cliked at" + position)
            if (mScanning) {
                mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                mScanning = false
            }
            val device = mLeDeviceListAdapter!!.getDevice(position)
            mDeviceAddress = device!!.address
            mDeviceName = device.name
            Log.d(ConnectivityUtils.TAG, "Clicked device name" + mDeviceName)
            Log.d(ConnectivityUtils.TAG, "Clicked device address" + mDeviceAddress)

            viewModel.isDeviceListLayout.set(false)
            viewModel.isProjectorLayout.set(true)
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            GlobalScope.launch {
                delay(2000)
                isBinded = bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
                Log.d(ConnectivityUtils.TAG, "Bind BluetoothLeService")
            }
            startBleWaiting()

        }

    }
    //WIFI SERVICES

    fun initNSD() {
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager?
        Log.d(ConnectivityUtils.TAG, "NSD Initialized")
//        initializeResolveListener()
    }

    inner class MyResolveListener:NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo:NsdServiceInfo, errorCode:Int) {
            Log.d(ConnectivityUtils.TAG, "Resolve failed$errorCode")
        }

        override fun onServiceResolved(serviceInfo:NsdServiceInfo) {
            Log.d(ConnectivityUtils.TAG, "Resolve Succeeded. $serviceInfo")

            mService = serviceInfo
            onServiceFound(serviceInfo.serviceName)

        }
    }


    fun discoverServices() {
        Log.d(ConnectivityUtils.TAG, "NSD - discoverServices()")
        stopDiscovery()
        initializeDiscoveryListener()
        nsdManager?.discoverServices(
            NSD_SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )
    }

    fun initializeDiscoveryListener() {
        Log.d(ConnectivityUtils.TAG, "NSD - initializeDiscoveryListener()")
        discoveryListener = object : NsdManager.DiscoveryListener {

            override fun onDiscoveryStarted(regType: String) {
                Log.d(ConnectivityUtils.TAG, "Service discovery started " +regType)
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(ConnectivityUtils.TAG, "Service discovery success $service")
                Log.d(ConnectivityUtils.TAG, "trying to resolve $service")
                if (service.serviceName == "PROJECTOR_SERVICE"){
                    stopDiscovery()
                    nsdManager?.resolveService(service, MyResolveListener())
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.d(ConnectivityUtils.TAG, "service lost$service")
                if (mService == service) {
                    mService = null
                }
            }

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            override fun onDiscoveryStopped(serviceType: String) {
                Log.d(ConnectivityUtils.TAG, "Discovery stopped: $serviceType")
                //discoveryStopped()
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.d(ConnectivityUtils.TAG, "Discovery failed: Error code:$errorCode")
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.d(ConnectivityUtils.TAG, "Discovery failed: Error code:$errorCode")
            }
        }
    }

    fun stopDiscovery() {
        Log.d(ConnectivityUtils.TAG, "NSD - stopDiscovery()")
        if (discoveryListener != null) {
            try {
                nsdManager?.stopServiceDiscovery(discoveryListener)
            } finally {
            }
            discoveryListener = null
        }
    }

    fun getChosenServiceInfo(): NsdServiceInfo? {
        return mService
    }


    fun searchNSDservice() {
        Log.d(ConnectivityUtils.TAG, "searchNSDservice()")
        Utility.isServiceConnected = false
        discoverServices()
        startServiceTimer()
        //checkService()
    }

    private fun startServiceTimer(){
        Log.d(ConnectivityUtils.TAG, "startServiceTimer()")
        serviceConnectionWaitingJob = GlobalScope.launch {
            delay(6000)
            Log.d(ConnectivityUtils.TAG, "startServiceTimer() : inside")
            stopDiscovery()
            startBLESearch()
        }
    }

    fun checkService() {
        Log.d(ConnectivityUtils.TAG, "checkService()")
        GlobalScope.launch {
            delay(6000)
            if (isServiceFound) {
                isServiceFound = false
                checkSocketConnection()
            } else {
                stopDiscovery()
            }
        }
    }

    private fun checkSocketConnection(){
        GlobalScope.launch {
            delay(2000)
            startSocketConnection()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun onServiceFound(ServiceName: String) {
        Log.d(ConnectivityUtils.TAG, "OnServiceFound()")
        nsdservice = getChosenServiceInfo()
        isServiceFound = true
        Utility.isServiceConnected = isServiceFound
        Utility.nsdSericeHostName = nsdservice?.host?.hostAddress.toString()
        Utility.nsdSericePortName = nsdservice?.port!!.toInt()
        serviceConnectionWaitingJob?.cancel()
        //startSocketConnection()
        checkSocketConnection()

    }


    private suspend fun startSocketConnection() {
        Log.d(ConnectivityUtils.TAG, "startSocketConnection()")
        //runBlocking {
        withContext(Dispatchers.IO) {
            //val ipAddress: String = nsdservice?.host?.hostAddress.toString()
        val host: InetAddress = nsdservice?.host!!
            Log.d(ConnectivityUtils.TAG, "address" + host + " port " + nsdservice?.port)
            var soc: Socket? = null
            try {
                soc = Socket(host, nsdservice?.port!!.toInt())
                if (soc.isConnected){
                    Log.d(ConnectivityUtils.TAG, "Connected")
                    viewModel.isProjectorLayout.set(false)
                    Log.d(ConnectivityUtils.TAG, "connection sucess_move to calibration")
                    viewModel.isServiceError.set(false)
                    returnFromActivity("success")
                } else {
                    Log.d(ConnectivityUtils.TAG, "Socket connection failure")
                    viewModel.isServiceError.set(true)
                    viewModel.isDeviceListLayout.set(false)
                    viewModel.isWifiCredLayout.set(false)
                    viewModel.isProjectorLayout.set(false)
                    viewModel.isErrorLayout.set(true)
                }
            }
            catch (e: ConnectException) {
                Log.d(ConnectivityUtils.TAG, "Socket connection failure" +e)
                viewModel.isServiceError.set(false)
                viewModel.isDeviceListLayout.set(false)
                viewModel.isWifiCredLayout.set(false)
                viewModel.isProjectorLayout.set(false)
                viewModel.isErrorLayout.set(false)
                startBLESearch()
            }
            catch (e: Exception) {
                viewModel.isServiceError.set(true)
                viewModel.isDeviceListLayout.set(false)
                viewModel.isWifiCredLayout.set(false)
                viewModel.isProjectorLayout.set(false)
                viewModel.isErrorLayout.set(true)
                Log.d(ConnectivityUtils.TAG, "Exception " + e.message)
            } finally {
                soc?.close()
            }
        }
    }


    //BLE SERVICES
    // Code to manage Service lifecycle.
    private val mServiceConnection = object : ServiceConnection {

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            Log.d(ConnectivityUtils.TAG, "BluetoothLeService-onServiceConnected")
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService!!.initialize()) {
                Log.d(ConnectivityUtils.TAG, "Unable to initialize Bluetooth")
                finish()
            }
            Log.d(ConnectivityUtils.TAG, "BluetoothLeService-Connect to device address")
            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(ConnectivityUtils.TAG, "BluetoothLeService-onServiceDisconnected")
            mBluetoothLeService = null
        }
    }

    fun returnFromActivity(result: String) {
        val intent = Intent()
        var resultString: String? = result
        intent.setData(Uri.parse(resultString))
        setResult(RESULT_OK, intent)
        finish();
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun discoveryStopped() {
        Log.d(ConnectivityUtils.TAG, "WIFI Discovery stopped")
        startBLESearch()
    }


    //BLE and its Connection
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun startBLESearch() {
        Log.d(ConnectivityUtils.TAG, "startBLESearch()")
        if(isBinded) {
            Log.d(ConnectivityUtils.TAG, "UnBind BluetoothLeService")
            unbindService(mServiceConnection)
        }
        mLeDeviceListAdapter = LeDeviceListAdapter()
        runOnUiThread {
            deviceList!!.adapter = mLeDeviceListAdapter
            viewModel.isProjectorLayout.set(false)
            viewModel.isWifiCredLayout.set(false)
            viewModel.isErrorLayout.set(false)
            viewModel.isDeviceListLayout.set(true)
            //refresh.isClickable = false
        }
        scanLeDevice(true)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            this.mHandler!!.postDelayed({
                mScanning = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                    enableRefreshButton(true)
                }
            }, SCAN_PERIOD)

            enableRefreshButton(false)
            mScanning = true
            mBluetoothAdapter!!.startLeScan(mLeScanCallback)
        } else {
            mScanning = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                enableRefreshButton(true)
            }
        }
    }

    fun enableRefreshButton(refreshButton: Boolean){
        if(refreshButton){
            refresh.isClickable = true
            refresh.alpha = 1.0f
            viewModel.isProgressBar.set(false)
        }else{
            refresh.isClickable = false
            refresh.alpha = 0.5f
            viewModel.isProgressBar.set(true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initWIFIService() {
        if (allPermissionsGranted()) {
            searchNSDservice()
        } else {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }    // Device scan callback.
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        //viewModel.isProgressBar.set(false)
        //refresh.isClickable = true
        runOnUiThread {
            mLeDeviceListAdapter!!.addDevice(device)
            mLeDeviceListAdapter!!.notifyDataSetChanged()
        }
    }

    private inner class LeDeviceListAdapter() : BaseAdapter() {
        private val mLeDevices: ArrayList<BluetoothDevice>
        private val mInflator: LayoutInflater

        init {
            mLeDevices = ArrayList<BluetoothDevice>()
            mInflator = layoutInflater
        }

        fun addDevice(device: BluetoothDevice) {
            if (!mLeDevices.contains(device)) {
                val deviceName = device.name
                if (deviceName != null && deviceName.length > 0) {
                    mLeDevices.add(device)
                }
            }
        }

        fun getDevice(position: Int): BluetoothDevice? {
            return mLeDevices[position]
        }

        fun clear() {
            mLeDevices.clear()
        }

        override fun getCount(): Int {
            //Log.d(ConnectivityUtils.TAG, "BLE Devie count"+mLeDevices.size)
            return mLeDevices.size
        }

        override fun getItem(i: Int): Any {
            return mLeDevices[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view
            val viewHolder: ViewHolder
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null)
                viewHolder =
                    ViewHolder()
                viewHolder.deviceName = view.findViewById(R.id.device_name) as TextView
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }

            val device = mLeDevices[i]
            val deviceName = device.name
            if (deviceName != null && deviceName.length > 0)
                viewHolder.deviceName!!.text = deviceName
            else
                viewHolder.deviceName!!.setText(R.string.unknown_device)



            return view!!
        }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    @SuppressLint("ServiceCast")
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    internal class ViewHolder {
        var deviceName: TextView? = null
    }

    companion object {

        const val REQUEST_CODE_PERMISSIONS = 10
        const val NSD_SERVICE_TYPE: String = "_http._tcp."
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_HANDSHAKE_SUCCESS)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVER_SUCCESS)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVER_FAILURE)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_WIFI_FAILURE)
            return intentFilter
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onResume() {
        super.onResume()
        Log.d(ConnectivityUtils.TAG, "Activity goes to OnResume ")
        registerReceiver(mGattUpdateReceiver,
            makeGattUpdateIntentFilter()
        )
        getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* if (mBluetoothLeService != null && !isBLEConnected) {
             val result = mBluetoothLeService!!.connect(mDeviceAddress)
             //Log.d(TAG, "Connect request result=" + result)
         }*/
    }

    override fun onPause() {
        super.onPause()
        Log.d(ConnectivityUtils.TAG, "Activity goes to OnPause ")
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ConnectivityUtils.TAG, "Activity goes to OnDestroy ")
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun getGattServices(gattServices: List<BluetoothGattService>?) {
        Log.d(ConnectivityUtils.TAG, "Activity-getGattServices()")
        //if (gattServices == null) return
        if (gattServices != null) {
            for (gattService in gattServices) {
                if (gattService.uuid == ConnectivityUtils.SERVICE_UUID) {
                    mGattCharacteristics?.addAll(gattService.characteristics)
                    GlobalScope.launch {
                        delay(2000)
                        ConnectivityUtils.HANDSHAKE_REQUEST?.let {
                            mBluetoothLeService?.handshakeRequest(it)
                        }
                    }
                }
            }
        }

    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothLeService.ACTION_GATT_CONNECTED == action) {
                viewModel.isBLEConnected = true
                viewModel.isProjectorLayout.set(true)
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED == action) {
                viewModel.isBLEConnected = false
                viewModel.isProjectorLayout.set(false)
                viewModel.isDeviceListLayout.set(false)
                viewModel.isWifiCredLayout.set(false)
                viewModel.isErrorLayout.set(true)
                stopWaiting()
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {

                if (mBluetoothLeService?.supportedGattServices == null) {
                    viewModel.isProjectorLayout.set(false)
                    viewModel.isDeviceListLayout.set(false)
                    viewModel.isWifiCredLayout.set(false)
                    viewModel.isErrorLayout.set(true)
                    stopWaiting()
                } else {
                    viewModel.isProjectorLayout.set(true)
                    getGattServices(mBluetoothLeService!!.supportedGattServices)
                }
            } else if (BluetoothLeService.ACTION_GATT_HANDSHAKE_SUCCESS == action) {
                //--------------------------------------OPEN NETWORK CHANGES-------------------------------//
               /* if(!networkType.contains("WEP") && !networkType.contains("WPA")
                    && !networkType.contains("WPA2")) {
                    Log.d(ConnectivityUtils.TAG, "Send network SSID")
                    val ssidpwd = "$connSSID,,$networkType"
                    mBluetoothLeService?.connectWIFI(ssidpwd)!!
                    startWifiWaiting()
                } else {*/
                    viewModel.isProjectorLayout.set(false)
                    viewModel.isErrorLayout.set(false)
                    viewModel.isDeviceListLayout.set(false)
                    viewModel.isWifiCredLayout.set(true)
                    stopWaiting()
               // }
            } else if (BluetoothLeService.ACTION_GATT_SERVER_SUCCESS == action) {
                viewModel.isProjectorLayout.set(true)
                searchNSDservice()
                stopWaiting()
            } else if (BluetoothLeService.ACTION_GATT_SERVER_FAILURE == action) {
                viewModel.isProjectorLayout.set(true)
                viewModel.isDeviceListLayout.set(false)
                viewModel.isWifiCredLayout.set(false)
                viewModel.isErrorLayout.set(true)
                stopWaiting()
            } else if (BluetoothLeService.ACTION_GATT_WIFI_FAILURE == action) {
                viewModel.isWifiError.set(true)
                viewModel.isProjectorLayout.set(true)
                viewModel.isDeviceListLayout.set(false)
                viewModel.isWifiCredLayout.set(false)
                viewModel.isErrorLayout.set(true)
                stopWaiting()
            } else {

            }
        }
    }
//----------------------------Connection Timeout. Need to Check............................//
    private fun startWifiWaiting(){
        wifiConnectionWaitingJob = GlobalScope.launch {
            delay(25000)
            viewModel.isProjectorLayout.set(true)
            viewModel.isErrorLayout.set(true)
        }
    }
    private fun startBleWaiting(){
        bleConnectionWaitingJob = GlobalScope.launch {
            delay(25000)
            viewModel.isProjectorLayout.set(true)
            viewModel.isErrorLayout.set(true)
        }
    }

    private fun stopWaiting(){
        wifiConnectionWaitingJob?.cancel()
        bleConnectionWaitingJob?.cancel()
    }
//----------------------------Connection Timeout. Need to Check............................//
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun setUIEvents() {
        Log.d(ConnectivityUtils.TAG, "Setting UI Events")
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun handleEvent(event: ConnectivityViewModel.Event): Unit =
        when (event) {

            is ConnectivityViewModel.Event.OnCancelClicked -> {
                Log.d(ConnectivityUtils.TAG, "clicked cancel button in BLE list view")
                ConnectivityUtils.CANCEL?.let { returnFromActivity(it) }
                onBackPressed()
            }
            is ConnectivityViewModel.Event.OnCancelCredentialClicked -> {
                Log.d(ConnectivityUtils.TAG, "clicked cancel button in wifi credentials view")
                ConnectivityUtils.CANCEL?.let { returnFromActivity(it) }
                onBackPressed()
            }
            is ConnectivityViewModel.Event.OnCancelErrorClicked -> {
                Log.d(ConnectivityUtils.TAG, "clicked cancel button in unsucess/error view")
                ConnectivityUtils.CANCEL?.let { returnFromActivity(it) }
                onBackPressed()
            }
            is ConnectivityViewModel.Event.OnSkipClicked -> {
                Log.d(ConnectivityUtils.TAG, "clicked skip button in BLE list view")
                ConnectivityUtils.SKIP?.let { returnFromActivity(it) }
                onBackPressed()
            }
            is ConnectivityViewModel.Event.OnSkipCredentialsClicked -> {
                Log.d(ConnectivityUtils.TAG, "clicked skip button in wifi credentials view")
                hideKeyboard()
                ConnectivityUtils.SKIP?.let { returnFromActivity(it) }
                onBackPressed()
            }
            is ConnectivityViewModel.Event.OnConnectClicked -> {
                Log.d(ConnectivityUtils.TAG, "connect clicked in wifi credentials view")
                hideKeyboard()
                if (wifiname.text.toString() != null) {
                    val ssidpwd: String =
                        wifiname.text.toString() + "," + wifipwd.text.toString()
                    viewModel.isWifiCredLayout.set(false)
                    viewModel.isProjectorLayout.set(true)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        viewModel.isWifiError.set(false)
                        mBluetoothLeService?.connectWIFI(ssidpwd)!!
                        startWifiWaiting()
                    } else {
                        TODO("VERSION.SDK_INT < LOLLIPOP")
                    }
                } else {

                }
            }
            is ConnectivityViewModel.Event.OnRetryClicked -> {
                Log.d(ConnectivityUtils.TAG, "clicked retry button in unsucess/error view")
                viewModel.isErrorLayout.set(false)
                if (viewModel.isWifiError.get()){
                    viewModel.isProjectorLayout.set(false)
                    viewModel.isErrorLayout.set(false)
                    viewModel.isDeviceListLayout.set(false)
                    viewModel.isWifiCredLayout.set(true)
                } else if (viewModel.isServiceError.get()){
                   searchNSDservice()
                } else {
                    startBLESearch()
                }


            }
            is ConnectivityViewModel.Event.OnRefreshClicked -> {
                Log.d(ConnectivityUtils.TAG, "clicked refresh button in BLE list view")
                //Toast.makeText(this,"refresh clicked",Toast.LENGTH_SHORT).show()
                if (mScanning) {
                    mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                    mScanning = false
                }
                startBLESearch()
            }

        }

    fun getWIFIname() {
        try {
            val wifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo
            wifiInfo = wifiManager.connectionInfo
            if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                connSSID = wifiInfo.ssid.replace("\"", "")
            }
            val wifi: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val networkList: List<ScanResult> = wifi.scanResults
            if (networkList != null) {
                for (network in networkList) {
                    if (connSSID.equals(network.SSID)) {
            networkType = network.capabilities
                    }
                }
            }

        } catch (e: Exception) {

        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        this?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                turnGPSOn(object :
                    onGpsListener {
                    override fun gpsStatus(isGPSEnable: Boolean) {
                        viewModel.isLocationEnabled.set(isGPSEnable)
                        if(isGPSEnable){
                            Log.d(ConnectivityUtils.TAG,"GPS enabled")
                            getWIFIname()
                            wifiname.setText(connSSID)
                            searchNSDservice()
                        }else{
                            Log.d(ConnectivityUtils.TAG,"GPS disabled")
                        }
                    }
                })
            } else {
                Log.d(ConnectivityUtils.TAG, "Permission Denied by the user")
                Toast.makeText(
                    this,
                    "App will not work properly without this permission. Please turn on the permission from settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    ////////////////////////////
    fun turnGPSOn(onGpsListener: onGpsListener?) {

        var locationManager: LocationManager? = null
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var mSettingsClient: SettingsClient? = null
        mSettingsClient = LocationServices.getSettingsClient(this)

        lateinit var locationRequest: LocationRequest
        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        var mLocationSettingsRequest: LocationSettingsRequest? = null
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        mLocationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)

        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGpsListener?.gpsStatus(true)
        } else {
            mSettingsClient
                ?.checkLocationSettings(mLocationSettingsRequest)
                ?.addOnSuccessListener(
                    (this as Activity?)!!,
                    object :
                        OnSuccessListener<LocationSettingsResponse?> {
                        @SuppressLint("MissingPermission")


                        override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                            onGpsListener?.gpsStatus(true)
                        }
                    })
                ?.addOnFailureListener(
                    (this as Activity?)!!
                ) { e ->
                    val statusCode =
                        (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae =
                                e as ResolvableApiException
                            rae.startResolutionForResult(
                                this as Activity?,
                                GPS_REQUEST
                            )
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(
                                ConnectivityUtils.TAG,
                                "PendingIntent unable to execute request."
                            )
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings."
                            Log.e(ConnectivityUtils.TAG, errorMessage)
                            Toast.makeText(
                                this as Activity?,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }
    }

    interface onGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                viewModel.isLocationEnabled.set(true)
                getWIFIname()
                wifiname.setText(connSSID)
                initWIFIService()
            }
        }else{
            Log.d(ConnectivityUtils.TAG, "User Disabled GPS")
            viewModel.isProjectorLayout.set(false)
            viewModel.isLocationEnabled.set(false)
            finish()
        }
    }

    private fun setupKeyboardListener(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val t_guideline: View? = findViewById<View>(R.id.top_guide)
            val b_guideline: View? = findViewById<View>(R.id.bottom_guide)
            var t_params: ConstraintLayout.LayoutParams? = null
            t_params = t_guideline?.getLayoutParams() as ConstraintLayout.LayoutParams
            var b_params: ConstraintLayout.LayoutParams? = null
            b_params = b_guideline?.getLayoutParams() as ConstraintLayout.LayoutParams

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
            val actionBar: ActionBar? = actionBar
            if (actionBar != null) {
                actionBar.hide()
            }

            if (Math.abs(view.rootView.height - (r.bottom - r.top)) > (view.rootView.height/2)) { // if more than 100 pixels, its probably a keyboard...
                if(isTablet(this)){
                    t_params.guidePercent=0.0f
                    b_params.guidePercent=0.45f
                }else{
                    t_params.guidePercent=-0.05f
                    b_params.guidePercent=0.45f
                }
            } else {
                t_params.guidePercent=0.25f
                b_params.guidePercent=0.75f
            }

            t_guideline.setLayoutParams(t_params)
            b_guideline.setLayoutParams(b_params)
        }
    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = context.getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }
    


}



