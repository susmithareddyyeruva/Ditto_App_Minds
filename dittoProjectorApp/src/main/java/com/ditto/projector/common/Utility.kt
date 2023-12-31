package com.ditto.projector.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Base64
import android.util.Log
import org.apache.commons.lang3.ArrayUtils
import java.io.IOException
import java.math.BigInteger
import java.net.InetAddress
import java.net.ServerSocket
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Utility  {

    companion object {

        const val TRACE_PROJECTOR = "TRACE_PROJECTOR"
        const val KEY_WIFI = "WIFI"
        const val KEY_WIFI_CRED = "KEY_WIFI_CRED"

        private val CHARSET_NAME = "UTF-8"
        private val AES_NAME = "AES"
        val ALGORITHM = "AES/CBC/PKCS7Padding"
        val KEY = "3hb1L0x7*Ditto*i"
        val IV = "d16(}Ditt16(}Trc"

        fun decrypt(content: String): String? {
            try {
                val cipher = Cipher.getInstance(ALGORITHM)
                val keySpec =
                    SecretKeySpec(KEY.toByteArray(charset(CHARSET_NAME)), AES_NAME)
                val paramSpec: AlgorithmParameterSpec =
                    IvParameterSpec(IV.toByteArray())
                cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec)
                val data = Base64.decode( content, Base64.DEFAULT)
                return String(cipher.doFinal(data))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return ""
        }

        fun isWifiConnected(context: Context): Boolean {
            val connMgr  = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var isWifiConn: Boolean = false
            connMgr.allNetworks.forEach { network ->
                connMgr.getNetworkInfo(network)?.apply {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        isWifiConn = isWifiConn or isConnected
                    }
                }
            }
            return isWifiConn
        }

         fun connectToWifi(ssid: String, pwd: String, context: Context) {
            try {
                val conf = WifiConfiguration()
                conf.SSID = "\"" + ssid + "\""
                //--------------------------OPEN NETWORK CHANGES-------------------//
                if (pwd.length > 1) {
                    conf.preSharedKey = "\"" + pwd + "\""
                }else{
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }
                //conf.preSharedKey = "\"" + pwd + "\""
                val wifiManager =
                    context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiID = wifiManager.addNetwork(conf)
                /*val wifiConfiguration =
                    WifiConfiguration()
                wifiConfiguration.SSID = String.format("\"%s\"", ssid)
                //--------------------------OPEN NETWORK CHANGES-------------------//
                if (pwd.length > 1) {
                    wifiConfiguration.preSharedKey = String.format("\"%s\"", pwd)
                }else{
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }
                wifiConfiguration.preSharedKey = String.format("\"%s\"", pwd)
                val wifiID = wifiManager.addNetwork(wifiConfiguration)*/
                wifiManager.disconnect()
                wifiManager.enableNetwork(wifiID, true)
                wifiManager.reconnect()

            }catch (msg : Exception){

                Log.d("exception","Connected to wifi")
            }
        }

        fun disConnectToWifi(ssid: String, pwd: String, context: Context) {
            try {
                val conf = WifiConfiguration()
                conf.SSID = "\"" + ssid + "\""
                //--------------------------OPEN NETWORK CHANGES-------------------//
                if (pwd.length > 1) {
                    conf.preSharedKey = "\"" + pwd + "\""
                }else{
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }
                //conf.preSharedKey = "\"" + pwd + "\""
                val wifiManager =
                    context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiID = wifiManager.addNetwork(conf)
                wifiManager.removeNetwork(wifiID)
            }catch (msg : Exception){

                Log.d("exception","Connected to wifi")
            }
        }

        fun getIpaddress(context: Context) : String {

            val wifiMgr: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiMgr.connectionInfo
            val myIPAddress = BigInteger.valueOf(wifiInfo.ipAddress.toLong()).toByteArray()
            ArrayUtils.reverse(myIPAddress);
            val myInetIP = InetAddress.getByAddress(myIPAddress)
            val hostaddr = myInetIP.hostAddress
            return hostaddr
        }

        /**
         *   [Function] to find available port
         */
          fun findFreePort(): Int {
            var socket: ServerSocket? = null
            try {
                socket = ServerSocket(0)
                socket.reuseAddress = true
                val port = socket.localPort
                try {
                    socket.close()
                } catch (e: IOException) {
                    Log.d("TRACE", e.toString())
                }
                return port
            } catch (e: IOException) {
                Log.d("Exception","create Socket")
            } finally {
                if (socket != null) {
                    try {
                        socket.close()
                    } catch (e: IOException) {

                        Log.d("Exception","findFreePort")
                    }
                }
            }
            throw IllegalStateException("Could not find a free TCP/IP ")
        }

        fun setSharedPref(context: Context, id: String) {
            val sharedPreference =
                context.getSharedPreferences(TRACE_PROJECTOR, Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString(KEY_WIFI, id)
            editor.apply()
        }

        fun getSharedPref(context: Context): String? {
            val sharedPreference =
                context.getSharedPreferences(TRACE_PROJECTOR, Context.MODE_PRIVATE)
            return sharedPreference.getString(KEY_WIFI, "N/A")
        }

        fun setWificred(context: Context, id: String) {
            val sharedPreference =
                context.getSharedPreferences(TRACE_PROJECTOR, Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString(KEY_WIFI_CRED, id)
            editor.apply()
        }

        fun getWificred(context: Context): String? {
            val sharedPreference =
                context.getSharedPreferences(TRACE_PROJECTOR, Context.MODE_PRIVATE)
            return sharedPreference.getString(KEY_WIFI_CRED, "N/A")
        }
    }


}