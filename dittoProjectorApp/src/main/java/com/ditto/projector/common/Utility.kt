package com.ditto.projector.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import org.apache.commons.lang3.ArrayUtils
import java.io.IOException
import java.math.BigInteger
import java.net.InetAddress
import java.net.ServerSocket

class Utility  {

    companion object {

        const val TRACE_PROJECTOR = "TRACE_PROJECTOR"
        const val KEY_WIFI = "WIFI"

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
            } finally {
                if (socket != null) {
                    try {
                        socket.close()
                    } catch (e: IOException) {
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
    }


}