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

    override fun saveLong(key: String, value: Long) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putLong(key, value).commit()
    }

    override fun getLong(key: String): Long? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getLong(key, 0)
    }
    override fun saveInt(key: String, value: Int) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putInt(key, value).commit()

    }

    override fun clear() {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val clear = pref.edit().clear().apply()
    }

    override fun clearAllPreferenceExceptCoachMark(key1: String, key2: String) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val prefList: MutableMap<String, *> = pref.all
        if (prefList.containsKey(key1)) prefList.remove(key1)
        if (prefList.containsKey(key2)) prefList.remove(key2)

        for ((key) in prefList) {
            pref.edit().remove(key).apply()
        }
    }
}