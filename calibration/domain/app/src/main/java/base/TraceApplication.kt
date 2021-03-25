package base


import com.facebook.stetho.Stetho
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


/**
 * Application class
 */
class TraceApplication : DaggerApplication() {

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
    }
}