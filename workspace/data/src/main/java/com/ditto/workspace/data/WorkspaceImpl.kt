package com.ditto.workspace.data

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.GetWorkspaceData
import com.ditto.workspace.domain.WorkspaceRepository
import com.ditto.workspace.domain.model.*
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

    override fun insertWorkspaceData(workspaceData: WorkspaceDataAPI): Single<Any> {
       return workspaceRepository.insertWorkspaceData(workspaceData)
    }

    override fun getUserDetails(): Single<Result<LoginUser>> {
        return workspaceRepository.getUserData()
    }

    override fun deleteAndInsert(id: Int, patternsData: PatternsData): Single<Any> {
        return workspaceRepository.deleteAndInsert(id, patternsData)
    }

    override fun getWorkspaceData(): Single<Result<WorkspaceResultDomain>> {
        return workspaceRepository.getWorkspaceDataFromApi()
    }

    override fun updateWorkspaceData(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData): Single<Result<WSUpdateResultDomain>> {
        return workspaceRepository.updateWorkspaceDataFromApi(cTraceWorkSpacePatternInputData)

    }

}