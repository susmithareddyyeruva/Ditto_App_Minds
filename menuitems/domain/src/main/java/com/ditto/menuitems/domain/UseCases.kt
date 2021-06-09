package com.ditto.menuitems.domain

import com.ditto.login.domain.LoginUser
import com.ditto.menuitems.domain.model.WSSettingsInputData
import io.reactivex.Single
import non_core.lib.Result


interface GetWorkspaceProData{
    fun postSwitchData(data: WSSettingsInputData): Single<Boolean>
    fun getUserDetails():Single<Result<LoginUser>>
    fun updateWSProSetting(id:Int,cMirrorReminder:Boolean,cCuttingReminder:Boolean,cSpliceReminder:Boolean,cSpliceMultiplePieceReminder:Boolean):Single<Any>
}