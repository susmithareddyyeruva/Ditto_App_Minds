package non_core.lib.error

sealed class Error(
    val message: String,
    val throwable: Throwable?
)
open class RemoteConfigError(
    message: String = "Remote Config Fetching error",
    throwable: Throwable? = null
) : FeatureError(message, throwable)

open class UnknownError(
    message: String = "Unknown Error",
    throwable: Throwable? = null
) : Error(message, throwable)

open class NetworkError(
    message: String = "Network Error",
    throwable: Throwable?
) : Error(message, throwable)

open class NoNetworkError(
    message: String = "No Internet connection available !",
    throwable: Throwable? = null
) : Error(message, throwable)

/*
   Feature error class to be used by individual feature
 */
abstract class FeatureError(
    message: String,
    throwable: Throwable? = null
) : Error(message, throwable)

