package com.ditto.projector.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log


class WifiConnectionListener: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (connectivityReceiverListener != null) {
            Log.d("TRACE", "********* onReceive " )
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context!!),
                Utility.getSharedPref(context))
        }

    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean, wifiName: String?)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}