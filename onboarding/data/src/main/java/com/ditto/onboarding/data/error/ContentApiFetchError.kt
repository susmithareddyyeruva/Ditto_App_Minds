package com.ditto.onboarding.data.error

import non_core.lib.error.FeatureError

open class ContentApiFetchError(
    message: String = "Content Api fetch Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)