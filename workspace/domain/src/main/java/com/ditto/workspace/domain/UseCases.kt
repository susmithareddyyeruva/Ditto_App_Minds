package com.ditto.workspace.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.model.*
import io.reactivex.Single
import non_core.lib.Result

interface GetWorkspaceData {
    fun invoke(): Single<Result<List<PatternsData>>>
    fun insert(patternsData: PatternsData): Single<Any>//follow same
    fun insertWorkspaceData(w: WorkspaceDataAPI): Single<Any>//follow same
    fun getUserDetails():Single<Result<LoginUser>>
    fun deleteAndInsert(id: Int, patternsData: PatternsData): Single<Any>
    fun getWorkspaceData() : Single<Result<WorkspaceResultDomain>>
    fun updateWorkspaceData(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData) : Single<Result<WSUpdateResultDomain>>
}

