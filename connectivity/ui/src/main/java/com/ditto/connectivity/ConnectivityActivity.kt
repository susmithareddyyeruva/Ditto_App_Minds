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
import com.ditto.connectivity.databinding.ConnectivityActivityBinding
import com.ditto.connectivity.service.BluetoothLeService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import core.appstate.AppState
import core.models.Nsdservicedata
import core.network.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.connectivity_activity.*
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.InetAddress
import java.net.Socket

class ConnectivityActivity : AppCompatActivity(), core.ui.common.Utility.CustomCallbackDialogListener {

    private var mLeDeviceListAdapter: LeDeviceListAdapter? = null
    private var mServiceListAdapter: ServiceListAdapter? = null
    private var mHandler: Handler? = null
    private var mScanning: Boolean = false
    private var mPreviousServiceAvailable: Boolean = false
    private var mWifiServiceAvailable: Boolean = false
    private val SCAN_PERIOD: Long = 10000
    private var mDeviceAddress: String? = null
    private var mDeviceName: String? = null
    private var connSSID: String = ""
    private var networkType: String = ""
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val GPS_REQUEST = 1001
    lateinit var mClickedService  : Nsdservicedata
    val serviceList = ArrayList<Nsdservicedata>()
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
        showLayouts(false,false,false,false,true)
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
            OnGPSListener {
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
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            GlobalScope.launch {
                delay(2000)
                isBinded = bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
                Log.d(ConnectivityUtils.TAG, "Bind BluetoothLeService")
            }
            startBleWaiting()
            showLayouts(false,false,false,false,true)
        }


        deviceList_proj!!.setOnItemClickListener { parent, view, position, id ->

            showLayouts(false,false,false,false,true)
            viewModel.isServiceFoundAfterWifi.set(false)
            mClickedService = mServiceListAdapter!!.getDevice(position)!!
            isServiceFound = true
            Utility.isServiceConnected = isServiceFound
            Utility.nsdSericeHostName = mClickedService!!.nsdSericeHostAddress
            Utility.nsdSericePortName = mClickedService!!.nsdServicePort
            serviceConnectionWaitingJob?.cancel()
            checkSocketConnection()
        }

    }
    //WIFI SERVICES

    fun initNSD() {
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager?
        Log.d(ConnectivityUtils.TAG, "NSD Initialized")
    }

    inner class MyResolveListener:NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo:NsdServiceInfo, errorCode:Int) {
            Log.d(ConnectivityUtils.TAG, "Resolve failed$errorCode")
        }

        override fun onServiceResolved(serviceInfo:NsdServiceInfo) {
            Log.d(ConnectivityUtils.TAG, "Resolve Succeeded. $serviceInfo")
            stopDiscovery()
            mService = serviceInfo
            val nsdData = Nsdservicedata(
                mService!!.serviceName,
                mService?.host?.hostAddress.toString(),
                mService?.port!!.toInt()
            )
            if (viewModel.isServiceFoundAfterWifi.get()){
                connectServiceAfterWifi(nsdData)
            } else {
                serviceList.add(nsdData)
            }

        }
    }


    private fun discoverServices() {
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
                if (viewModel.isServiceFoundAfterWifi.get()){
                    if (service.serviceName == ConnectivityUtils.nsdSericeNameAfterWifi){
                        nsdManager?.resolveService(service, MyResolveListener())
                    }
                 } else {

                    if (service.serviceName.startsWith("DITTO")){
                        nsdManager?.resolveService(service, MyResolveListener())
                    }
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
                Log.d("discoveryListener","in final block")
            }
            discoveryListener = null
        }
    }

    fun getChosenServiceInfo(): NsdServiceInfo? {
        return mService
    }


    fun searchNSDservice() {
        Log.d(ConnectivityUtils.TAG, "searchNSDservice()")
        serviceList.clear()
        Utility.isServiceConnected = false
        discoverServices()
        startServiceTimer()
    }
    fun searchWifiNSDservice() {
        Log.d(ConnectivityUtils.TAG, "searchNSDservice()")
        serviceList.clear()
        Utility.isServiceConnected = false
        discoverServices()
    }

    private fun searchNSDserviceFromPopup() {
        Log.d(ConnectivityUtils.TAG, "searchNSDservice()")
        serviceList.clear()
        mServiceListAdapter?.notifyDataSetChanged()
        Utility.isServiceConnected = false
        discoverServices()
        startServiceTimer()
        viewModel.isNoServiceFound.set(false)
        viewModel.isProgressBar.set(true)
    }

    private fun nsdServiceAutoConnect(services : Nsdservicedata){
        isServiceFound = true
        Utility.isServiceConnected = isServiceFound
        Utility.nsdSericeHostName = services!!.nsdSericeHostAddress
        Utility.nsdSericePortName = services!!.nsdServicePort
        serviceConnectionWaitingJob?.cancel()
        mClickedService = services
        checkSocketConnection()
    }

    private fun startServiceTimer() {

        serviceConnectionWaitingJob = GlobalScope.launch {
            delay(6000)
            stopDiscovery()
            connectService()
        }
    }
    /* Search completed after successfully sharing the wifi ceredentials*/
    private fun connectServiceAfterWifi(mServiceData : Nsdservicedata) {
        viewModel.isServiceFoundAfterWifi.set(false)
        nsdServiceAutoConnect(mServiceData)
    }

    private fun connectService() {
        if (serviceList.isEmpty()) {   /* Showing BLE list if no serivce is found  */
            startBLESearch()
        } else {
            for (item in serviceList) { /* Loop to identify whether last connected service available */
                if (item.nsdServiceName == AppState.getLastSavedServiceName()) {
                    mPreviousServiceAvailable = true
                    mClickedService = item
                    nsdServiceAutoConnect(mClickedService)
                    break
                }
            }
            if (!mPreviousServiceAvailable) { /* Check whether the last connected service found or not */
                mPreviousServiceAvailable = false
                populateServiceList()
                showLayouts(true, false, false, false, false)
            }
        }
    }

    private fun populateServiceList(){
        mServiceListAdapter = ServiceListAdapter(serviceList)
        runOnUiThread {
            deviceList_proj.adapter = mServiceListAdapter
        }
    }

    private fun checkSocketConnection(){
        GlobalScope.launch {
            delay(2000)
            startSocketConnection()
        }
    }


    private suspend fun startSocketConnection() {
        Log.d(ConnectivityUtils.TAG, "startSocketConnection()")
        //runBlocking {
        withContext(Dispatchers.IO) {
            val host = InetAddress.getByName(mClickedService.nsdSericeHostAddress)
            var soc: Socket? = null
            try {
                soc = Socket(host, mClickedService.nsdServicePort)
                if (soc.isConnected) {
                    AppState.clearSavedService()
                    AppState.saveCurrentService(mClickedService)
                    viewModel.isServiceError.set(false)
                    showLayouts(false, false, false, true, false)

                } else {
                    viewModel.isServiceError.set(true)
                    showLayouts(false, false, false, true, false)
                }
            } catch (e: ConnectException) {
                viewModel.isServiceError.set(true)
                showLayouts(false, false, false, true, false)
            } catch (e: Exception) {
                viewModel.isServiceError.set(true)
                showLayouts(false, false, false, true, false)
            } finally {
                soc?.close()
            }
        }
    }


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
            showLayouts(false, true, false, false, false)

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


    private inner class ServiceListAdapter(services : ArrayList<Nsdservicedata>) : BaseAdapter() {
        var mServiceList: ArrayList<Nsdservicedata> = services
        private val mInflator: LayoutInflater = layoutInflater


        fun getDevice(position: Int): Nsdservicedata? {
            return mServiceList[position]
        }


        override fun getCount(): Int {
            //Log.d(ConnectivityUtils.TAG, "BLE Devie count"+mLeDevices.size)
            return mServiceList.size
        }

        override fun getItem(i: Int): Any {
            return mServiceList[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            var view = view
            val viewHolder: ViewHolder
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.service_list_item, null)
                viewHolder =
                    ViewHolder()
                viewHolder.deviceName = view.findViewById(R.id.service_name) as TextView
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }

            val device = mServiceList[i]
            val deviceName = device.nsdServiceName
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
                showLayouts(false, false, false, false, true)

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED == action) {
                viewModel.isBLEConnected = false
                showLayouts(false, false, false, true, false)
                stopWaiting()
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED == action) {

                if (mBluetoothLeService?.supportedGattServices == null) {
                    showLayouts(false, false, false, true, false)
                    stopWaiting()
                } else {
                    showLayouts(false, false, false, false, true)
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
                showLayouts(false, false, true, false, false)
                stopWaiting()
               // }
            } else if (BluetoothLeService.ACTION_GATT_SERVER_SUCCESS == action) {
                showLayouts(false, false, false, false, true)
                viewModel.isServiceFoundAfterWifi.set(true)
                searchWifiNSDservice()
                stopWaiting()
            } else if (BluetoothLeService.ACTION_GATT_SERVER_FAILURE == action) {
                showLayouts(false, false, false, true, false)
                stopWaiting()
            } else if (BluetoothLeService.ACTION_GATT_WIFI_FAILURE == action) {
                viewModel.isWifiError.set(true)
                showLayouts(false, false, false, true, false)
                stopWaiting()
            } else {
                Log.d("BroadcastReceiver","Action Gatt server undefined")
            }
        }
    }
//----------------------------Connection Timeout. Need to Check............................//
    private fun startWifiWaiting(){
        wifiConnectionWaitingJob = GlobalScope.launch {
            delay(25000)
            showLayouts(false, false, false, true, false)
        }
    }
    private fun startBleWaiting(){
        bleConnectionWaitingJob = GlobalScope.launch {
            delay(25000)
            showLayouts(false, false, false, true, false)
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
                     var encryptedcred: String = ""
                    val encryptedssid: String? = ConnectivityUtils.encrypt(wifiname.text.toString())
                    val encryptedpwd: String? = ConnectivityUtils.encrypt(wifipwd.text.toString())
                    val encryptedAuth: String? = ConnectivityUtils.encrypt("ANDROID")
                    if (!encryptedssid.equals("") && !encryptedpwd.equals("")){
                        encryptedcred = "$encryptedssid,$encryptedpwd,$encryptedAuth"
                    } else {
                        encryptedcred = wifiname.text.toString() + "," + wifipwd.text.toString()+ ","+"ANDROID"
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        viewModel.isWifiError.set(false)
                        mBluetoothLeService?.connectWIFI(encryptedcred)!!
                        startWifiWaiting()
                    } else {
                       // TODO("VERSION.SDK_INT < LOLLIPOP")
                    }
                    showLayouts(false, false, false, false, true)
                } else {
                    Log.d("error","instruction error")
                }
                Unit
            }
            is ConnectivityViewModel.Event.OnRetryClicked -> {
                if (viewModel.isServiceError.get()){
                    showLayouts(false, false, false, false, true)
                    searchNSDservice()
                } else {
                    returnFromActivity("success")
                }
                /*Log.d(ConnectivityUtils.TAG, "clicked retry button in unsucess/error view")
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
                }*/


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
            ConnectivityViewModel.Event.OnProjRefreshClicked -> searchNSDserviceFromPopup()
            ConnectivityViewModel.Event.OnProjScanViaBleClicked -> startBLESearch()
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
            Log.d("exception","wifi")
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                turnGPSOn(object :
                    OnGPSListener {
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
    fun turnGPSOn(onGpsListener: OnGPSListener?) {

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

    interface OnGPSListener {
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
            showLayouts(false, false, false, false, false)
            viewModel.isLocationEnabled.set(false)
            finish()
        }
    }

    private fun setupKeyboardListener(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val topGuideline: View? = findViewById<View>(R.id.top_guide)
            val bottomGuideline: View? = findViewById<View>(R.id.bottom_guide)
            var topParams: ConstraintLayout.LayoutParams? = null
            topParams = topGuideline?.getLayoutParams() as ConstraintLayout.LayoutParams
            var bottomParams: ConstraintLayout.LayoutParams? = null
            bottomParams = bottomGuideline?.getLayoutParams() as ConstraintLayout.LayoutParams

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
                    topParams.guidePercent=0.0f
                    //bottomParams.guidePercent=0.45f
                }else{
                    topParams.guidePercent=-0.12f
                    //bottomParams.guidePercent=0.45f
                }
            } else {
                topParams.guidePercent=0.15f
                //bottomParams.guidePercent=0.75f
            }

            topGuideline.setLayoutParams(topParams)
            //bottomGuideline.setLayoutParams(bottomParams)
        }
    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = context.getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    private fun showConnectionSuccessPopup(){

        core.ui.common.Utility.getCommonAlertDialogue(
            this,
            "",
            "Successfully connected!",
            "",
            "OK",
            this,
            core.ui.common.Utility.AlertType.CONNECTIVITY,
            core.ui.common.Utility.Iconype.SUCCESS
        )

    }


    private fun showFailurePopup(){
        core.ui.common.Utility.getCommonAlertDialogue(
            this,
            "",
            "Connection Failed!",
            "CANCEL",
            "RETRY",
            this,
            core.ui.common.Utility.AlertType.CONNECTIVITY,
            core.ui.common.Utility.Iconype.FAILED
        )

    }

    override fun onCustomPositiveButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {
        if (iconype == core.ui.common.Utility.Iconype.SUCCESS){
            returnFromActivity("success")
        } else {
            searchNSDservice()
        }
    }

    override fun onCustomNegativeButtonClicked(
        iconype: core.ui.common.Utility.Iconype,
        alertType: core.ui.common.Utility.AlertType
    ) {

    }
    private fun showLayouts(isShowServiceListLayout : Boolean, isShowBleListLayout : Boolean, isShowWifiCredLayout : Boolean,isShowErrorLayout : Boolean ,isShowLootie : Boolean ){
        viewModel.isProjectorLayout.set(isShowLootie)
        viewModel.isWifiCredLayout.set(isShowWifiCredLayout)
        viewModel.isErrorLayout.set(isShowErrorLayout)
        viewModel.isShowServiceList.set(isShowServiceListLayout)
        viewModel.isDeviceListLayout.set(isShowBleListLayout)
    }
}



