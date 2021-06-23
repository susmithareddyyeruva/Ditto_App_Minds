package com.ditto.workspace.data

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.GetWorkspaceData
import com.ditto.workspace.domain.WorkspaceRepository
import com.ditto.workspace.domain.model.PatternsData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject


class WorkspaceImpl @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) : GetWorkspaceData {
    override fun invoke(): Single<Result<List<PatternsData>>> {
        return workspaceRepository.getWorkspaceData()
    }

    override fun insert(patternsData: PatternsData): Single<Any> {
        return workspaceRepository.insertData(patternsData)
    }

    override fun getUserDetails(): Single<Result<LoginUser>> {
        return workspaceRepository.getUserData()
    }

    override fun deleteAndInsert(id: Int, patternsData: PatternsData): Single<Any> {
        return workspaceRepository.deleteAndInsert(id, patternsData)
    }

}