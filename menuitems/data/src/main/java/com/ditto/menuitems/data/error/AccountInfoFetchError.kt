package com.ditto.menuitems.data.error

import non_core.lib.error.FeatureError

open class AccountInfoFetchError(
    message: String = "Account Info delete",
    throwable: Throwable? = null
) : FeatureError(message, throwable)
