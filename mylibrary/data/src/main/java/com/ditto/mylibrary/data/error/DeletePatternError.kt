package com.ditto.mylibrary.data.error

import non_core.lib.error.FeatureError

open class DeletePatternError(
    message: String = "Failed to delete Pattern from DB",
    throwable: Throwable? = null
):FeatureError(message,throwable)