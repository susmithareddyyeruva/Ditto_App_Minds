package com.ditto.mylibrary.data.error

import non_core.lib.error.FeatureError

open class TrialPatternError(
    message: String = "Failed to Fetch the Trial patterns",
    throwable: Throwable? = null
): FeatureError(message, throwable)