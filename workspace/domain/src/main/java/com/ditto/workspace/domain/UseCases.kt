package com.ditto.workspace.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.model.PatternsData
import io.reactivex.Single
import non_core.lib.Result

interface GetWorkspaceData {
    fun invoke(): Single<Result<List<PatternsData>>>
    fun insert(patternsData: PatternsData): Single<Any>
    fun getUserDetails():Single<Result<LoginUser>>
    fun deleteAndInsert(id: Int, patternsData: PatternsData): Single<Any>
}

