package com.ditto.mylibrary.ui.util

object StringUtil {
    fun formatCategory(s:String):String{
        val result = StringBuilder()
        for (i in 0 until s.length) {
            val c = s[i]
            if (i != 0 && Character.isUpperCase(c)) {
                result.append(' ')
            }
            result.append(c)
        }
        return  result.toString()
    }
}