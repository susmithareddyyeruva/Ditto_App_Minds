package core.appstate

interface PreferenceStorage {
    fun saveString(key:String,value:String)
    fun getString(key:String):String?
    fun getInt(key:String):Int?
    fun getBoolean(key: String):Boolean?
    fun saveBoolean(key:String,value:Boolean)
    fun saveLong(key:String,value:Long)
    fun getLong(key:String):Long?
    fun saveInt(key:String,value:Int)
    fun clear()
}