package com.ditto.mylibrary.data.error

import non_core.lib.error.FeatureError

class TailornovaAPIError(
    message:String="There is internal server error",
    throwable: Throwable?=null
):FeatureError(message, throwable)
