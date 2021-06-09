package com.ditto.menuitems.domain

import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result

interface WorkspaceProRepository {
    fun getUserData(): Single<Result<LoginUser>>
    fun updateWSProSetting(id : Int,cMirrorReminder: Boolean,cCuttingReminder: Boolean,cSpliceReminder: Boolean,cSpliceMultiplePieceReminder: Boolean): Single<Any>
}