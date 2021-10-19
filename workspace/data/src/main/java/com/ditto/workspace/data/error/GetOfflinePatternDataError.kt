package com.ditto.workspace.data.error

import non_core.lib.error.FeatureError

open class GetOfflinePatternDataError(
    message: String = "Get Patterns Data DB fetch failed",
    throwable : Throwable ? = null
) : FeatureError(message, throwable)
