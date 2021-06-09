package com.ditto.menuitems_ui.settings.domain

import com.ditto.menuitems_ui.settings.data.LoginResult
import com.ditto.menuitems_ui.settings.model.WSSettingsInputData
import io.reactivex.Single
import non_core.lib.Result


/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface SettingsRepository {
    fun postSwitchData(data: WSSettingsInputData): Single<Result<LoginResult>>

}