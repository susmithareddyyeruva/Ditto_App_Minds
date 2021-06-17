package com.ditto.workspace.domain

import com.ditto.login.domain.LoginUser
import non_core.lib.Result
import io.reactivex.Single
import com.ditto.workspace.domain.model.PatternsData

interface GetWorkspaceData {
    fun invoke(): Single<Result<List<PatternsData>>>
    fun insert(patternsData: PatternsData): Single<Any>
    fun getUserDetails():Single<Result<LoginUser>>
    fun deleteAndInsert(id: Int, patternsData: PatternsData): Single<Any>
}

