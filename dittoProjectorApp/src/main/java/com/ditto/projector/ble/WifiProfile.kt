package com.ditto.projector.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class WifiProfile {

    var WIFI_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")
    var CREDENTIALS_CHARACTERISTICS = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb")
    var BUFFER_SERVICE = UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb")
    var CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun buildWifiService(): BluetoothGattService? {
        val wifiservice =
            BluetoothGattService(
                WIFI_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )


        val credentialscharactersitics =
            BluetoothGattCharacteristic(
                CREDENTIALS_CHARACTERISTICS,  //Read-only characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
            )
        val configDescriptor =
            BluetoothGattDescriptor(
                CLIENT_CONFIG,  //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE
            )
        credentialscharactersitics.addDescriptor(configDescriptor)
        wifiservice.addCharacteristic(credentialscharactersitics)
        //wifiservice.addCharacteristic(budderservice)
        return wifiservice
    }
}