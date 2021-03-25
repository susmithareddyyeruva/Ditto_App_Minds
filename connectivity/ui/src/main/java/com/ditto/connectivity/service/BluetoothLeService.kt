package com.ditto.connectivity.service

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.connectivity.ConnectivityUtils
import com.ditto.connectivity.ConnectivityUtils.Companion.CHARACTERISTIC_UUID
import com.ditto.connectivity.ConnectivityUtils.Companion.SERVICE_UUID
import java.util.*
import javax.inject.Inject


class BluetoothLeService : Service() {

    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mConnectionState =
        STATE_DISCONNECTED
    private var mMtu = 20
    val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(BluetoothLeService::class.java.simpleName)
    }


    private val mGattCallback = @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(ConnectivityUtils.TAG, "onConnectionStateChange()")
            var intentAction: String = ""
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(ConnectivityUtils.TAG, "onConnectionStateChange() - STATE_CONNECTED")
                intentAction =
                    ACTION_GATT_CONNECTED
                mConnectionState =
                    STATE_CONNECTED
                Log.d(ConnectivityUtils.TAG, "discover GATT Services" + " discoverServices()")
                mBluetoothGatt!!.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(ConnectivityUtils.TAG, "onConnectionStateChange() - STATE_DISCONNECTED")
                intentAction =
                    ACTION_GATT_DISCONNECTED
                mConnectionState =
                    STATE_DISCONNECTED

            } else {

            }
            broadcastUpdate(intentAction)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(ConnectivityUtils.TAG, "onServicesDiscovered()")
            var intentAction: String = ""
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(ConnectivityUtils.TAG, "onServicesDiscovered()-GATT SUCCESS")
                intentAction =
                    ACTION_GATT_SERVICES_DISCOVERED
                changeMtu(512)
           //
              /*  val gattService =
                    gatt.getService(UUID.fromString("00001805-0000-1000-8000-00805f9b34fb"))
                val gattCharacteristic = gattService.getCharacteristic(
                    UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb")
                )
                gatt.setCharacteristicNotification(gattCharacteristic, true)
                val descriptor = gattCharacteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                )
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)*/
                //
            } else {
                intentAction =
                    ACTION_GATT_DISCONNECTED
                Log.d(ConnectivityUtils.TAG, "onServicesNotDiscovered()-GATT FAILURE")
            }
            broadcastUpdate(intentAction)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            Log.d(
                ConnectivityUtils.TAG,
                "onCharacteristicChanged(): " + String(characteristic.value)
            )
            var intentAction: String = ""
            if (String(characteristic.value).equals(ConnectivityUtils.HANDSHAKE_RESPONSE)) {
                
                Log.d(
                    ConnectivityUtils.TAG,
                    "onCharacteristicChanged():HANDSHAKE RESPONSE - $ACTION_GATT_HANDSHAKE_SUCCESS")
                intentAction =
                    ACTION_GATT_HANDSHAKE_SUCCESS
            } else if (String(characteristic.value).equals(ConnectivityUtils.SERVER_SUCCESS_RESPONSE) || String(characteristic.value).equals(
                    ConnectivityUtils.SERVice_ALREADY_REGISTERED)) {
                Log.d(
                    ConnectivityUtils.TAG,
                    "onCharacteristicChanged():SERVER RESPONSE - $ACTION_GATT_SERVER_SUCCESS")

                intentAction =
                    ACTION_GATT_SERVER_SUCCESS
            } else if (String(characteristic.value).equals(ConnectivityUtils.WIFIFAILS)){

                Log.d(
                    ConnectivityUtils.TAG,
                    "onCharacteristicChanged():SERVER RESPONSE - $ACTION_GATT_WIFI_FAILURE")
                intentAction =
                    ACTION_GATT_WIFI_FAILURE
            } else {
                Log.d(
                    ConnectivityUtils.TAG,
                    "onCharacteristicChanged():SERVER RESPONSE - $ACTION_GATT_SERVER_FAILURE")
                intentAction =
                    ACTION_GATT_SERVER_FAILURE
            }
            broadcastUpdate(intentAction)
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            Log.d(ConnectivityUtils.TAG, "BluetoothDevice   onMtuChanged")
            mMtu = mtu
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.d(ConnectivityUtils.TAG, "onCharacteristicWrite()")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            Log.d(ConnectivityUtils.TAG, "onDescriptorWrite()")
        }


    }



    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun handshakeRequest(request: String) {
        Log.d(ConnectivityUtils.TAG, "handshakeRequest()" + request)
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return
        }
        val mCustomService = mBluetoothGatt!!.getService(ConnectivityUtils.SERVICE_UUID) ?: return
        val mWriteCharacteristic =
            mCustomService.getCharacteristic(ConnectivityUtils.CHARACTERISTIC_UUID)
        mWriteCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        mBluetoothGatt!!.setCharacteristicNotification(mWriteCharacteristic, true)
        mWriteCharacteristic.value = request.toByteArray()
        mBluetoothGatt!!.writeCharacteristic(mWriteCharacteristic)

        val uuid: UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)
        val descriptor = mWriteCharacteristic.getDescriptor(uuid).apply {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }
        mBluetoothGatt!!.writeDescriptor(descriptor)

        //---------------commented by vishnu-----------//

        /*val uuid: UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)
        val descriptor = mWriteCharacteristic.getDescriptor(uuid).apply {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }
        mBluetoothGatt!!.writeDescriptor(descriptor)

        val uuid1: UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)
        val descriptor1 = mWriteCharacteristic.getDescriptor(uuid1).apply {
            value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        }
        mBluetoothGatt!!.writeDescriptor(descriptor1)*/

        //--------------------------//

        /*if (mBluetoothGatt?.setCharacteristicNotification(mWriteCharacteristic, true)!!) {
            val descriptor: BluetoothGattDescriptor =
                mWriteCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            if (descriptor != null) {
                if (mWriteCharacteristic.getProperties() and BluetoothGattCharacteristic.PROPERTY_NOTIFY !== 0) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else if (mWriteCharacteristic.getProperties() and BluetoothGattCharacteristic.PROPERTY_INDICATE !== 0) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                } else {
                    // The characteristic does not have NOTIFY or INDICATE property set;
                }
                if (mBluetoothGatt!!.writeDescriptor(descriptor)) {
                    // Success
                } else {
                    // Failed to set client characteristic notification;
                }
            } else {
                // Failed to set client characteristic notification;
            }
        } else {
            // Failed to register notification;
        }
*/
       /* val descriptor: BluetoothGattDescriptor = mWriteCharacteristic.getDescriptor(
            UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb")
        )*/


        /*if (UUID_WRITE == mWriteCharacteristic.uuid) {
            val descriptor = mWriteCharacteristic.getDescriptor(
                UUID.fromString(CHARACTERISTIC_UUID.toString())
            )
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            mBluetoothGatt!!.writeDescriptor(descriptor)
        }*/



        Log.d(ConnectivityUtils.TAG, "handshakeRequest()" + request)
        Log.d(ConnectivityUtils.TAG, "handshakeRequest()" + request.toByteArray())
        Log.d(ConnectivityUtils.TAG, "handshakeRequest()" + mWriteCharacteristic)
    }

    /**
     * Enables or disables notification on a give characteristic.
     * @param characteristic Characteristic to act on.
     * *
     * @param enabled If true, enable notification.  False otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(ConnectivityUtils.TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.setCharacteristicNotification(characteristic, enabled)

        if (UUID_WRITE == characteristic.uuid) {
            val descriptor = characteristic.getDescriptor(
                UUID.fromString(CHARACTERISTIC_UUID.toString())
            )
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            mBluetoothGatt!!.writeDescriptor(descriptor)
        }

        /*if (mBluetoothGatt?.setCharacteristicNotification(characteristic, true)!!) {
            val descriptor: BluetoothGattDescriptor =
                characteristic.getDescriptor(ConnectivityUtils.SERVICE_UUID)
            if (descriptor != null) {
                if (characteristic.getProperties() and BluetoothGattCharacteristic.PROPERTY_NOTIFY !== 0) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else if (characteristic.getProperties() and BluetoothGattCharacteristic.PROPERTY_INDICATE !== 0) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                } else {
                    // The characteristic does not have NOTIFY or INDICATE property set;
                }
                if (mBluetoothGatt!!.writeDescriptor(descriptor)) {
                    // Success
                } else {
                    // Failed to set client characteristic notification;
                }
            } else {
                // Failed to set client characteristic notification;
            }
        } else {
            // Failed to register notification;
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun connectWIFI(credentials: String) {
        Log.d(ConnectivityUtils.TAG, "connectWIFI()" + credentials)
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return
        }
        val mCustomService = mBluetoothGatt!!.getService(ConnectivityUtils.SERVICE_UUID)
        if (mCustomService == null) {
            return
        }

        val mWriteCharacteristic =
            mCustomService.getCharacteristic(ConnectivityUtils.CHARACTERISTIC_UUID)
        mWriteCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        mBluetoothGatt!!.setCharacteristicNotification(mWriteCharacteristic, true)
        mWriteCharacteristic.value = credentials.toByteArray()
        mBluetoothGatt!!.writeCharacteristic(mWriteCharacteristic)

        val uuid: UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)
        val descriptor = mWriteCharacteristic.getDescriptor(uuid).apply {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }
        mBluetoothGatt!!.writeDescriptor(descriptor)

        val uuid1: UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)
        val descriptor1 = mWriteCharacteristic.getDescriptor(uuid1).apply {
            value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        }
        mBluetoothGatt!!.writeDescriptor(descriptor1)
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun changeMtu(size: Int) {
        Log.d(ConnectivityUtils.TAG, "changeMtu()" + size)
        mBluetoothGatt!!.requestMtu(size)
    }

    private fun broadcastUpdate(action: String) {
        Log.d(ConnectivityUtils.TAG, "broadcastUpdate()")
        val intent = Intent(action)
        sendBroadcast(intent)
    }


    inner class LocalBinder : Binder() {
        internal val service: BluetoothLeService
            get() = this@BluetoothLeService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onUnbind(intent: Intent): Boolean {
        close()
        return super.onUnbind(intent)
    }

    private val mBinder = LocalBinder()


    /**
     * Initializes a reference to the local Bluetooth adapter.
     * @return Return true if the initialization is successful.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun initialize(): Boolean {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            if (mBluetoothManager == null) {
                Log.d(ConnectivityUtils.TAG, "Unable to initialize BluetoothManager.")
                return false
            }
        }

        mBluetoothAdapter = mBluetoothManager!!.adapter
        if (mBluetoothAdapter == null) {
            Log.d(ConnectivityUtils.TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }

        return true
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null || address == null) {
            Log.d(ConnectivityUtils.TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress
            && mBluetoothGatt != null
        ) {
            Log.d(ConnectivityUtils.TAG, "Trying to use an existing mBluetoothGatt for connection.")
            if (mBluetoothGatt!!.connect()) {
                mConnectionState =
                    STATE_CONNECTING
                return true
            } else {
                return false
            }
        }

        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        if (device == null) {
            Log.d(ConnectivityUtils.TAG, "Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        Log.d(ConnectivityUtils.TAG, "Trying to create a new connection.")
        mBluetoothDeviceAddress = address
        mConnectionState =
            STATE_CONNECTING
        return true
    }


    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun disconnect() {
        Log.d(ConnectivityUtils.TAG, "disconnect()")
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(ConnectivityUtils.TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.disconnect()
    }


    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun close() {
        Log.d(ConnectivityUtils.TAG, "close()")
        if (mBluetoothGatt == null) {
            return
        }
        mBluetoothGatt!!.close()
        mBluetoothGatt = null
    }


    /**
     * Request a read on a given `BluetoothGattCharacteristic`. The read result is reported
     * asynchronously through the `BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)`
     * callback.
     * @param characteristic The characteristic to read from.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        Log.d(ConnectivityUtils.TAG, "readCharacteristic()")
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(ConnectivityUtils.TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.readCharacteristic(characteristic)
    }


    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.
     * @return A `List` of supported services.
     */
    val supportedGattServices: List<BluetoothGattService>?
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {
            if (mBluetoothGatt == null) return null
            Log.d(ConnectivityUtils.TAG, "supportedGattServices")
            return mBluetoothGatt!!.services
        }

    companion object {
        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2

        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_GATT_HANDSHAKE_SUCCESS =
            "com.example.bluetooth.le.ACTION_GATT_HANDSHAKE_SUCCESS"
        const val ACTION_GATT_SERVER_SUCCESS = "com.example.bluetooth.le.ACTION_GATT_SERVER_SUCCESS"
        const val ACTION_GATT_SERVER_FAILURE = "com.example.bluetooth.le.ACTION_GATT_SERVER_FAILURE"
        const val ACTION_GATT_WIFI_FAILURE = "com.example.bluetooth.le.ACTION_GATT_WIFI_FAILURE"
        val UUID_WRITE = UUID.fromString(SERVICE_UUID.toString())
    }
}