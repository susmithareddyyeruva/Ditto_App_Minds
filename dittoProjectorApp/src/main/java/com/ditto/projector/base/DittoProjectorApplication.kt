package com.ditto.projector.base

import android.app.Application
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.ditto.projector.BuildConfig

class DittoProjectorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCenter.start(
            this, BuildConfig.APP_CENTER_KEY ,
            Analytics::class.java, Crashes::class.java
        )
    }
}