package com.ditto.mylibrary.data.error

open class TailornovaInsertError(
    message: String =" Failed to insert Tailornova response",
    throwable: Throwable?=null
): FilterError(message, throwable)
