package com.ditto.menuitems.domain.model

import non_core.lib.error.FeatureError

open class WsSettingsPostError (
    message: String = "Login Failed",
    throwable: Throwable? = null
    ) : FeatureError(message, throwable)
