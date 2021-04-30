package core.appstate

import android.content.Context

class PreferenceStorageImpl(private val context: Context) :PreferenceStorage{
    companion object{
        private const val PREF_NAME="Ditto"
    }
    override fun saveString(key: String, value: String) {
       val pref=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        pref.edit().putString(key,value).commit()
    }

    override fun getString(key: String): String? {
        val pref=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
       return pref.getString(key,null)
    }

}