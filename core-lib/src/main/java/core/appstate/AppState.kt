package core.appstate

import android.content.Context

object AppState {
    private var pref: PreferenceStorage? = null
    private const val FIRST_RUN = "IS_FIRST_RUN"
    private const val KEY_EMAIL = "email"
    private const val KEY_MOBILE = "mobile"
    private const val KEY_FIRST_NAME = "firstname"
    private const val KEY_LAST_NAME = "lastname"

    fun init(context: Context) {
        pref = PreferenceStorageImpl(context)
    }

    fun getEmail(): String? {
        val email = pref?.getString(KEY_EMAIL)
        return email
    }

    fun getMobile(): String? {
        val mob = pref?.getString(KEY_MOBILE)
        return mob
    }
    fun getFirstName(): String? {
        val first = pref?.getString(KEY_FIRST_NAME)
        return first
    }
    fun getLastName(): String? {
        val lastName = pref?.getString(KEY_LAST_NAME)
        return lastName
    }
    fun saveMobile(mobile: String) {
        pref?.saveString(KEY_MOBILE, mobile)
    }

    fun saveEmail(email: String) {
        pref?.saveString(KEY_EMAIL, email)
    }

    fun saveFirstName(firstName: String) {
        pref?.saveString(KEY_FIRST_NAME, firstName)
    }

    fun saveLastName(lastName: String) {
        pref?.saveString(KEY_LAST_NAME, lastName)
    }
}