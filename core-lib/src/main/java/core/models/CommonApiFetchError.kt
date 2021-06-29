package core.models

import non_core.lib.error.FeatureError


open class CommonApiFetchError(
    message: String = "Content Api fetch Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)