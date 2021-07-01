package com.ditto.workspace.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.model.PatternsData
import com.ditto.workspace.domain.model.WSUpdateResultDomain
import com.ditto.workspace.domain.model.WorkspaceResultDomain
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface WorkspaceRepository {
    fun getWorkspaceData(): Single<Result<List<PatternsData>>>
    fun insertData(patternsData: PatternsData): Single<Any>
    fun deleteAndInsert(id:Int, patternsData: PatternsData): Single<Any>
    fun getUserData(): Single<Result<LoginUser>>
    fun getWorkspaceDataFromApi(): Single<Result<WorkspaceResultDomain>>
    fun updateWorkspaceDataFromApi(): Single<Result<WSUpdateResultDomain>>
}