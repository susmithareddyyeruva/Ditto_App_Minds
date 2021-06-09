package com.ditto.menuitems.data

import com.ditto.login.domain.LoginUser
import com.ditto.menuitems.domain.GetWorkspaceProData
import com.ditto.menuitems.domain.WorkspaceProRepository
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetWorkspaceProImpl @Inject constructor(
    private val workspaceProRepository: WorkspaceProRepository
): GetWorkspaceProData{
    override fun getUserDetails(): Single<Result<LoginUser>> {
        return workspaceProRepository.getUserData()
    }

    override fun updateWSProSetting(
        id: Int,
        cMirrorReminder: Boolean,
        cCuttingReminder: Boolean,
        cSpliceReminder: Boolean,
        cSpliceMultiplePieceReminder: Boolean
    ): Single<Any> {
        return workspaceProRepository.updateWSProSetting(id, cMirrorReminder, cCuttingReminder, cSpliceReminder, cSpliceMultiplePieceReminder)
    }
}