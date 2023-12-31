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

    override fun insert(patternsData: PatternsData): Single<Any> {
        return workspaceRepository.insertData(patternsData)
    }

    override fun updateOfflineStorageData(
        tailornaovaDesignId: String?,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfPieces?,
        patternPieces: List<PatternPieceSFCCAPI>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        liningWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        interfaceWorkspaceItems:MutableList<WorkspaceItemDomain>?,
        otherWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        notes: String?
    ): Single<Int> {
        return workspaceRepository.updateOfflineStorageData(tailornaovaDesignId,selectedTab,status,numberOfCompletedPiece,patternPieces,
        garmetWorkspaceItems,liningWorkspaceItems,interfaceWorkspaceItems, otherWorkspaceItems, notes)
    }

    override fun insertWorkspaceData(workspaceDataAPI: WorkspaceDataAPI): Single<Any> {
       return workspaceRepository.insertWorkspaceData(workspaceDataAPI)
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

    override fun getWorkspaceData(id: String): Single<Result<WorkspaceDataAPI>> {
        return workspaceRepository.getWorkspaceDataFromApi(id)
    }

    override fun updateWorkspaceData(id: String, workspaceDataAPI: WorkspaceDataAPI): Single<Result<WSUpdateResultDomain>> {
        return workspaceRepository.updateWorkspaceDataFromApi(id,workspaceDataAPI)
    }

    override fun createWorkspaceData(id: String, workspaceDataAPI: WorkspaceDataAPI): Single<Result<WSUpdateResultDomain>> {
     return workspaceRepository.createWorkspaceDataFromApi(id,workspaceDataAPI)
    }

}