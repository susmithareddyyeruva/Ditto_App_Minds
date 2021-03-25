package com.ditto.logger

import android.os.Build
import timber.log.Timber

class LoggerFactory {
    init {
        if (Build.VERSION.RELEASE == null) {
            if (!Timber.forest().contains(AppUnitTestTree)) {
                Timber.plant(AppUnitTestTree)
            }
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }

    fun create(tag: CharSequence): Logger {
        return LoggerImpl(tag)
    }
}

private class LoggerImpl(val tag: CharSequence) : Logger {
    override fun v(message: CharSequence, throwable: Throwable?) {
        Timber.tag(tag.toString())
        Timber.v(throwable, message.toString())
    }

    override fun d(message: CharSequence, throwable: Throwable?) {
        Timber.tag(tag.toString())
        Timber.d(throwable, message.toString())
    }

    override fun i(message: CharSequence, throwable: Throwable?) {
        Timber.tag(tag.toString())
        Timber.i(throwable, message.toString())
    }

    override fun w(message: CharSequence, throwable: Throwable?) {
        Timber.tag(tag.toString())
        Timber.w(throwable, message.toString())
    }

    override fun e(message: CharSequence, throwable: Throwable?) {
        Timber.tag(tag.toString())
        Timber.e(throwable, message.toString())
    }
}

private object AppUnitTestTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        println(message)
        throwable?.let { println(throwable) }
    }

}