package com.ditto.login.data.error

import non_core.lib.error.FeatureError

open class LandingContentFetchError(
    message: String = "Landing Content Fetch Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)
