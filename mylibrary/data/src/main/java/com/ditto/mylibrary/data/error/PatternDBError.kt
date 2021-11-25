package com.ditto.mylibrary.data.error

open class PatternDBError(
    message: String = "Failed to fetch patterns in DB",
    throwable: Throwable? = null
): FilterError(message,throwable)