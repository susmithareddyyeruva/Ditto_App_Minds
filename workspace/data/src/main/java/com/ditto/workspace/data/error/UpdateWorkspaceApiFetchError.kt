package com.ditto.workspace.data.error

import non_core.lib.error.FeatureError

open class UpdateWorkspaceApiFetchError (
    message: String = "Update Workspace Data Api fetch Failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)