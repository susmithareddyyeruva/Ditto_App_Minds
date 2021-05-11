package core.appstate

interface PreferenceStorage {
    fun saveString(key:String,value:String)
    fun getString(key:String):String?
    fun getBoolean(key: String):Boolean?
    fun saveBoolean(key:String,value:Boolean)
    fun saveLong(key:String,value:Long)
    fun getLong(key:String):Long?
    fun clear()
}