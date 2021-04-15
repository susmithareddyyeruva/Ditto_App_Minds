package com.ditto.projector.wifi

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.ditto.projector.ui.ProjectorConnectionActivity
import kotlin.properties.Delegates

class NsdHelper(applicationContext: Context) {
    var mContext: Context? = applicationContext
    var mNsdManager: NsdManager? = null
    val SERVICE_TYPE = "_http._tcp."
    var mServiceName = "PROJECTOR_SERVICE"
    private lateinit var mServiceHostname : String
    private var mServicePort by Delegates.notNull<Int>()
    var mRegistrationListener: NsdManager.RegistrationListener? = null
    fun initialize() {
        mNsdManager =
            mContext?.getSystemService(Context.NSD_SERVICE) as NsdManager
    }
    fun registerService(port: Int) {
        tearDown()
        initializeRegistrationListener()
        val serviceInfo = NsdServiceInfo()
        serviceInfo.port = port
        serviceInfo.serviceName = mServiceName
        serviceInfo.serviceType = SERVICE_TYPE
        mNsdManager = (mContext?.getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener)
        }
    }
    fun initializeRegistrationListener(){
        mRegistrationListener = object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
                mServiceName = nsdServiceInfo.serviceName
                //(mContext as ConnectionActivity).onNsdServiceRegistered(mServiceName)
                (mContext as ProjectorConnectionActivity).onNsdServiceRegistered(mServiceName)
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {

            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {

            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {

            }
        }
    }
    fun tearDown() {

        if (mRegistrationListener != null) {
            try {
                mNsdManager!!.unregisterService(mRegistrationListener)
            } finally {
                Log.d("tearDown", "in final block of code")
            }
            mRegistrationListener = null
        }
    }
}