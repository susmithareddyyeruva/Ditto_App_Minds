package core.appstate

import android.content.Context
import core.*
import core.models.Nsdservicedata

object AppState {
    private var pref: PreferenceStorage? = null
    private const val IS_SHOWN_COACH_MARK = "shownCoachMark"
    private const val IS_SHOWN_WORKSPACE_COACH_MARK = "shownWorkspaceCoachMark"
    private const val KEY_IS_LOGGED = "logged"
    private const val KEY_TOKEN = "token"
    private const val KEY_COUNT = "count"
    private const val KEY_TOKEN_EXPIRY = "token_expiry_time"
    private const val APP_VERSION = ""

    fun init(context: Context) {
        pref = PreferenceStorageImpl(context)
    }

    fun getToken(): String? {
        val token = pref?.getString(KEY_TOKEN)
        return token
    }

    fun saveToken(token: String, time: Long) {
        pref?.saveString(KEY_TOKEN, token)
        pref?.saveLong(KEY_TOKEN_EXPIRY, time)
    }

    fun getIsLogged(): Boolean {
        val isGuest = pref?.getBoolean(KEY_IS_LOGGED)
        return isGuest ?: false
    }

    fun setIsLogged(guest: Boolean) {
        pref?.saveBoolean(KEY_IS_LOGGED, guest)
    }

    fun isShownCoachMark(): Boolean {
        return pref?.getBoolean(IS_SHOWN_COACH_MARK) ?: false
    }

    fun setShowCoachMark(showCoachMark: Boolean) {
        pref?.saveBoolean(IS_SHOWN_COACH_MARK, showCoachMark)
    }

    fun isShownWorkspaceCoachMark(): Boolean {
        return pref?.getBoolean(IS_SHOWN_WORKSPACE_COACH_MARK) ?: false
    }

    fun setShowWorkspaceCoachMark(showCoachMark: Boolean) {
        pref?.saveBoolean(IS_SHOWN_WORKSPACE_COACH_MARK, showCoachMark)
    }

    fun logout() {
        pref?.clearAllPreferenceExceptCoachMark(IS_SHOWN_COACH_MARK, IS_SHOWN_WORKSPACE_COACH_MARK)
        //pref?.clear()
    }

    fun getExpiryTime(): Long? {
        val expTime = pref?.getLong(KEY_TOKEN_EXPIRY)
        return expTime
    }

    fun setCustID(customerID: String) {
        pref?.saveString(CUST_ID, customerID)
    }

    fun setCustNumber(custno: String) {
        pref?.saveString(CUST_NO, custno)
    }

    fun getCustID(): String? {
        val custid = pref?.getString(CUST_ID)
        return custid
    }

    fun getCustNO(): String? {
        val custNo = pref?.getString(CUST_NO)
        return custNo
    }

    fun getEmail(): String {
        val email = pref?.getString(USER_EMAIL)
        return email ?: ""
    }

    fun setEmail(email: String) {
        pref?.saveString(USER_EMAIL, email)
    }

    fun saveCurrentService(service: Nsdservicedata) {
        pref?.saveString(CONNECTED_SERVICE_NAME, service.nsdServiceName)
        pref?.saveString(CONNECTED_SERVICE_HOST, service.nsdSericeHostAddress)
        pref?.saveInt(CONNECTED_SERVICE_PORT, service.nsdServicePort)
    }

    fun clearSavedService() {
        pref?.saveString(CONNECTED_SERVICE_NAME, "")
        pref?.saveString(CONNECTED_SERVICE_HOST, "")
        pref?.saveInt(CONNECTED_SERVICE_PORT, 0)
    }

    fun getLastSavedServiceName(): String? {
        return pref?.getString(CONNECTED_SERVICE_NAME)
    }

    fun getLastSavedServicePort(): Int? {
        return pref?.getInt(CONNECTED_SERVICE_PORT)
    }

    fun getLastSavedServiceHost(): String? {
        return pref?.getString(CONNECTED_SERVICE_HOST)
    }

    fun setPatternCount(count: Int) {
        pref?.saveInt(PATTERN_COUNT, count)
    }

    fun getPatternCount(): Int? {
        val count = pref?.getInt(PATTERN_COUNT)
        return count
    }

    fun getAppVersion(): String? {
        val appversion = pref?.getString(APP_VERSION)
        return appversion
    }

    fun saveAppVersion(version: String) {
        pref?.saveString(APP_VERSION, version)
    }
}