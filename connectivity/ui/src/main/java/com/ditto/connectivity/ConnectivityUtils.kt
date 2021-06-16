package com.ditto.connectivity

import android.util.Base64
import org.jetbrains.annotations.NotNull
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ConnectivityUtils {
    companion object {
        private val CHARSET_NAME = "UTF-8"
        private val AES_NAME = "AES"
        val ALGORITHM = "AES/CBC/PKCS7Padding"
        val KEY = "3hb1L0x7*Ditto*i"
        val IV = "d16(}Ditt16(}Trc"
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
        var nsdSericeNameAfterWifi: String = ""
        fun encrypt(@NotNull content: String): String? {
            var result: ByteArray? = null
            var encryptresult: String? = null
            try {
                val cipher = Cipher.getInstance(ALGORITHM)
                val keySpec =
                    SecretKeySpec(KEY.toByteArray(charset(CHARSET_NAME)), AES_NAME)
                val paramSpec: AlgorithmParameterSpec =
                    IvParameterSpec(IV.toByteArray())
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec)
                result = cipher.doFinal(content.toByteArray(charset(CHARSET_NAME)))
                encryptresult = Base64.encodeToString(result, Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
                encryptresult = ""
            }
            return encryptresult
        }
    }

}