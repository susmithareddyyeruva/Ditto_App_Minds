package com.ditto.menuitems.data

import com.ditto.login.domain.model.LoginUser
import com.ditto.menuitems.domain.GetWorkspaceProData
import com.ditto.menuitems.domain.WorkspaceProRepository
import com.ditto.menuitems.domain.model.WSProSettingDomain
import com.ditto.menuitems.domain.model.WSSettingsInputData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetWorkspaceProImpl @Inject constructor(
    private val workspaceProRepository: WorkspaceProRepository
): GetWorkspaceProData{

    override fun postSwitchData(data: WSSettingsInputData): Single<Result<WSProSettingDomain>> {
        return workspaceProRepository.postSwitchData(data)
    }

    override fun getUserDetails(): Single<Result<LoginUser>> {
        return workspaceProRepository.getUserData()
    }

    override fun updateWSProSetting(
        id: Int,
        cMirrorReminder: Boolean,
        cCuttingReminder: Boolean,
        cSpliceReminder: Boolean,
        cSpliceMultiplePieceReminder: Boolean,
        cSaveCalibrationPhotos: Boolean
    ): Single<Any> {
        return workspaceProRepository.updateWSProSetting(id, cMirrorReminder, cCuttingReminder, cSpliceReminder, cSpliceMultiplePieceReminder,cSaveCalibrationPhotos)
    }
}