package com.ditto.storage.data

import android.content.Context
import com.ditto.storage.domain.StorageManager
import javax.inject.Inject

class StorageManagerImpl @Inject constructor(
    context: Context
) : StorageManager {

    private var prefs = context.getSharedPreferences(PREFS_FILE_APP, Context.MODE_PRIVATE)

    override fun savePrefs(key: String, value: Any) {
        val editor = prefs.edit()
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is String -> editor.putString(key, value)
            is Float -> editor.putFloat(key, value)
            is Int -> editor.putInt(key, value)
        }
        editor.apply()
    }

    override fun getBooleanValue(key: String): Boolean {
        return prefs.getBoolean(key, true)
    }

    override fun getStringValue(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun getFloatValue(key: String): Float {
        return prefs.getFloat(key, 0F)
    }

    override fun getIntValue(key: String): Int {
        return prefs.getInt(key, 0)
    }
}