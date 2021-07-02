package com.ditto.mylibrary.data.error

import non_core.lib.error.FeatureError

open class FilterError(
    message: String = "Failed to Fetch the data",
    throwable: Throwable? = null
) : FeatureError(message, throwable)