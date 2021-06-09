package com.ditto.menuitems_ui.settings.domain

import com.ditto.menuitems_ui.settings.UseCases
import com.ditto.menuitems_ui.settings.data.LoginResult
import com.ditto.menuitems_ui.settings.model.WSSettingsInputData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetWsSettingUseCaseImpl @Inject constructor(
    private val wsSettingsRepository: SettingsRepository
) : UseCases {

    override fun postSwitchData(data: WSSettingsInputData): Single<Result<LoginResult>> {
       return wsSettingsRepository.postSwitchData(data)
    }


}