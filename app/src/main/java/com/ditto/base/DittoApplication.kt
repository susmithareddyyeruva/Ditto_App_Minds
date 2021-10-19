package com.ditto.base


import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.ditto.BuildConfig
import com.ditto.base.core.di.DaggerAppComponent
import com.facebook.stetho.Stetho
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import core.appstate.AppState
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


/**
 * Application class
 */
class DittoApplication : DaggerApplication() {

    //Dagger lib initialization
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .setContext(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        /*To view data in db
        type chrome://inspect and select device and sql under resources*/
        Stetho.initializeWithDefaults(this);

        AppCenter.start(
            this, BuildConfig.APP_CENTER_KEY ,
            Analytics::class.java, Crashes::class.java
        )
        AppState.init(this)
        setAppVersion()
    }

    private fun setAppVersion() {
        try {
            val pInfo: PackageInfo =
                this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
            val version = pInfo.versionName
            AppState.saveAppVersion(version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}