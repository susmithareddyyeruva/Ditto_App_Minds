package com.ditto.storage.domain

interface StorageManager {

    fun savePrefs(key: String, value: Any)

    fun getBooleanValue(key: String): Boolean

    fun getStringValue(key: String): String?

    fun getFloatValue(key: String): Float

    fun getIntValue(key: String): Int
}