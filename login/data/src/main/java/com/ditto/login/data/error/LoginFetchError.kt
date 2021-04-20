package com.ditto.login.data.error

import non_core.lib.error.FeatureError

open class LoginFetchError(
    message: String = "Login Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)

