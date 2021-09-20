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

    override fun updateOfflineStorageData(
        tailornaovaDesignId: String,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfPieces?,
        patternPieces: List<PatternPieceSFCCAPI>,
        garmetWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        liningWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        interfaceWorkspaceItems:MutableList<WorkspaceItemDomain>?
    ): Single<Int> {
        return workspaceRepository.updateOfflineStorageData(tailornaovaDesignId,selectedTab,status,numberOfCompletedPiece,patternPieces,
        garmetWorkspaceItems,liningWorkspaceItems,interfaceWorkspaceItems)
    }

    override fun insertWorkspaceData(workspaceData: WorkspaceDataAPI): Single<Any> {
       return workspaceRepository.insertWorkspaceData(workspaceData)
    }

    override fun getPatternDataByID(id: Int):Single<Result<PatternsData>>  {
        return workspaceRepository.getPatternDataByID(id)
    }

    override fun getTailernovaDataByID(id: String): Single<Result<OfflinePatternData>> {
        return workspaceRepository.getTailernovaDataByID(id)
    }

    override fun getUserDetails(): Single<Result<LoginUser>> {
        return workspaceRepository.getUserData()
    }

    override fun deleteAndInsert(id: String, patternsData: PatternsData): Single<Any> {
        return workspaceRepository.deleteAndInsert(id, patternsData)
    }

    override fun getWorkspaceData(id: String): Single<Result<CTraceWorkSpacePatternDomain>> {
        return workspaceRepository.getWorkspaceDataFromApi(id)
    }

    override fun updateWorkspaceData(id: String,cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData): Single<Result<WSUpdateResultDomain>> {
        return workspaceRepository.updateWorkspaceDataFromApi(id,cTraceWorkSpacePatternInputData)
    }

    override fun createWorkspaceData(id: String,cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData): Single<Result<WSUpdateResultDomain>> {
     return workspaceRepository.createWorkspaceDataFromApi(id,cTraceWorkSpacePatternInputData)
    }

}