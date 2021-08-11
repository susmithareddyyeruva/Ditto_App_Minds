package com.ditto.data.error

import non_core.lib.error.FeatureError

open class HomeDataFetchError(
    message: String = "Failed to Fetch the data",
    throwable: Throwable? = null
) : FeatureError(message, throwable)