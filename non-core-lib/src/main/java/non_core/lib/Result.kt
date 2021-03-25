package non_core.lib

import non_core.lib.error.Error

sealed class Result<T> {

    data class OnSuccess<T>(val data: T) : Result<T>()
    data class OnError<T>(val error: Error) : Result<T>()

    companion object {
        /**
         * A simple utility method creating an instance of [OnSuccess] class and casting it into
         * base [Result].
         *
         * Can be used in some situations for avoiding explicit casts.
         */
        @JvmStatic
        fun <T> withValue(data: T): Result<T> = OnSuccess(data)

        /**
         * A simple utility method creating an instance of [OnError] class and casting it into
         * base [Result].
         *
         * Can be used in some situations for avoiding explicit casts.
         */
        @JvmStatic
        fun <T> withError(error: Error): Result<T> = OnError(error)
    }

    override fun toString(): String {
        return when (this) {
            is OnSuccess<*> -> "Success[data=$data]"
            is OnError -> "Error[error=$error]"
        }
    }
}
