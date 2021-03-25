package non_core.lib.error

sealed class Error(
    val message: String,
    val throwable: Throwable?
)

open class UnknownError(
    message: String = "Unknown Error",
    throwable: Throwable? = null
) : Error(message, throwable)

open class NetworkError(
    message: String = "Network Error",
    throwable: Throwable?
) : Error(message, throwable)

open class NoNetworkError(
    message: String = "No Network Error",
    throwable: Throwable? = null
) : Error(message, throwable)

/*
   Feature error class to be used by individual feature
 */
abstract class FeatureError(
    message: String,
    throwable: Throwable? = null
) : Error(message, throwable)