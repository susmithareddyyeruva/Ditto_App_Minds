package com.ditto.menuitems_ui.settings

import com.ditto.menuitems_ui.settings.data.LoginResult
import com.ditto.menuitems_ui.settings.model.WSSettingsInputData
import io.reactivex.Single
import non_core.lib.Result

interface UseCases {
    fun postSwitchData(data: WSSettingsInputData): Single<Result<LoginResult>>

}