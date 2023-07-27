package core.language

import android.content.Context
import androidx.multidex.MultiDexApplication
//Added by vineetha for switch language popup
class Application : MultiDexApplication() {

    companion object{


        lateinit  var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        Application.appContext = applicationContext
        appContext = applicationContext
    }





}