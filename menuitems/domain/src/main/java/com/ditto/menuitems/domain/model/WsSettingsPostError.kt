package com.ditto.menuitems.domain.model

import non_core.lib.error.FeatureError

open class WsSettingsPostError (
    message: String = "WS Settings API Fetch error",
    throwable: Throwable? = null
    ) : FeatureError(message, throwable)
