package com.ditto.data.error

import non_core.lib.error.FeatureError

open class HomeDataFetchError(
    message: String = "Home Data  fetch Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)