package core.appstate

interface PreferenceStorage {
    fun saveString(key:String,value:String)
    fun getString(key:String):String?
}