package com.ditto.connectivity

import java.util.*

class ConnectivityUtils {
    companion object {
        var CHARACTERISTIC_UUID = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb")
        var SERVICE_UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")
        var TAG:String?="TRACE_APP_CONNECTIONS"
        var CANCEL:String?="cancel"
        var SKIP:String?="skip"
        var HANDSHAKE_REQUEST:String?="TRACERequest"
        var HANDSHAKE_RESPONSE:String?="TRACEbleConnected"
        var SERVER_SUCCESS_RESPONSE:String?="SERVICESUCCESS"
        var SERVice_ALREADY_REGISTERED:String?="ServiceAlreadyRegistered"
        var WIFIFAILS:String?="TRACEFailedWifi"
    }

}