package core.appstate

import android.content.Context

class PreferenceStorageImpl(private val context: Context) : PreferenceStorage {
    companion object {
        private const val PREF_NAME = "Ditto"
    }

    override fun saveString(key: String, value: String) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putString(key, value).commit()
    }

    override fun getString(key: String): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(key, "")
    }

    override fun getInt(key: String): Int? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(key, 0)
    }

    override fun getBoolean(key: String): Boolean? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(key, false)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean(key, value).commit()
    }

    override fun saveInt(key: String, value: Int) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putInt(key, value).commit()
    }

    override fun clear() {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val clear = pref.edit().clear().apply()
    }

}