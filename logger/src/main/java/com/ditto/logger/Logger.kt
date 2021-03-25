package com.ditto.logger

interface Logger {
    fun v(message: CharSequence, throwable: Throwable? = null)
    fun d(message: CharSequence, throwable: Throwable? = null)
    fun i(message: CharSequence, throwable: Throwable? = null)
    fun w(message: CharSequence, throwable: Throwable? = null)
    fun e(message: CharSequence, throwable: Throwable? = null)
}