package core.appstate

import android.content.Context
import core.CONNECTED_SERVICE_HOST
import core.CONNECTED_SERVICE_NAME
import core.CONNECTED_SERVICE_PORT
import core.models.Nsdservicedata

object AppState {
    private var pref: PreferenceStorage? = null
    private const val KEY_IS_LOGGED = "logged"
    private const val KEY_TOKEN = "token"

    fun init(context: Context) {
        pref = PreferenceStorageImpl(context)
    }

    fun getToken(): String? {
        val token = pref?.getString(KEY_TOKEN)
        return token
    }

    fun saveToken(token: String) {
        pref?.saveString(KEY_TOKEN, token)
    }
    fun getIsLogged(): Boolean {
        val isGuest = pref?.getBoolean(KEY_IS_LOGGED)
        return isGuest ?: false
    }

    fun setIsLogged(guest: Boolean) {
        pref?.saveBoolean(KEY_IS_LOGGED, guest)
    }

    fun logout() {
        pref?.clear()

    }

    fun saveCurrentService(service : Nsdservicedata){
        pref?.saveString(CONNECTED_SERVICE_NAME,service.nsdServiceName)
        pref?.saveString(CONNECTED_SERVICE_HOST,service.nsdSericeHostAddress)
        pref?.saveInt(CONNECTED_SERVICE_PORT, service.nsdServicePort)
    }

    fun clearSavedService(){
        pref?.saveString(CONNECTED_SERVICE_NAME,"")
        pref?.saveString(CONNECTED_SERVICE_HOST,"")
        pref?.saveInt(CONNECTED_SERVICE_PORT, 0)
    }

    fun getLastSavedServiceName() : String?{
        return pref?.getString(CONNECTED_SERVICE_NAME)
    }

    fun getLastSavedServicePort() : Int?{
        return pref?.getInt(CONNECTED_SERVICE_PORT)
    }
    fun getLastSavedServiceHost() : String?{
        return pref?.getString(CONNECTED_SERVICE_HOST)
    }
}