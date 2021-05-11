package core.data.model

import non_core.lib.error.FeatureError

open class TokenFetchError(
    message: String = "Login Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)
