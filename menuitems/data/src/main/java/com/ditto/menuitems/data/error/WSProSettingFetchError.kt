package com.ditto.menuitems.data.error

import non_core.lib.error.FeatureError

open class WSProSettingFetchError(
    message: String = "Update WSProSetting fetch Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)