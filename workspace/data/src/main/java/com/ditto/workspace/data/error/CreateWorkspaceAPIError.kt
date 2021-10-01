package com.ditto.workspace.data.error

import non_core.lib.error.FeatureError

open class CreateWorkspaceAPIError(
    message: String = "Create Workspace API failed",
    throwable: Throwable? = null
) : FeatureError(message, throwable)
