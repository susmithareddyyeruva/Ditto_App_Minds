package core.appstate

import android.content.Context

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
}