package core.appstate

import android.content.Context
import core.*
import core.models.Nsdservicedata

object AppState {
    private var pref: PreferenceStorage? = null
    private const val KEY_IS_LOGGED = "logged"
    private const val KEY_TOKEN = "token"
    private const val KEY_TOKEN_EXPIRY = "token_expiry_time"

    fun init(context: Context) {
        pref = PreferenceStorageImpl(context)
    }

    fun getToken(): String? {
        val token = pref?.getString(KEY_TOKEN)
        return token
    }

    fun saveToken(token: String, time : Long) {
        pref?.saveString(KEY_TOKEN, token)
        pref?.saveLong(KEY_TOKEN_EXPIRY,time)
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

    fun getExpiryTime() : Long? {
        val expTime = pref?.getLong(KEY_TOKEN_EXPIRY)
        return expTime
    }

    fun setCustID(customerID: String) {
        pref?.saveString(CUST_ID, customerID)
    }

    fun getCustID(): String? {
        val custid = pref?.getString(CUST_ID)
        return custid
    }

    fun getEmail(): String? {
        val email = pref?.getString(USER_EMAIL)
        return email
    }

    fun setEmail(email: String) {
        pref?.saveString(USER_EMAIL, email)
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
    fun setPatternCount(count: Int) {
        pref?.saveInt(PATTERN_COUNT, count)
    }

    fun getPatternCount(): Int? {
        val count = pref?.getInt(PATTERN_COUNT)
        return count
    }
}