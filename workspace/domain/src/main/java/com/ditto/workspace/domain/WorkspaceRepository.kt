package com.ditto.workspace.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.model.*
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface WorkspaceRepository {
    fun insertData(patternsData: PatternsData): Single<Any>
    fun insertWorkspaceData(patternsDataAPI: WorkspaceDataAPI): Single<Any>
    //fun updateOfflineStorageData(tailornaovaDesignId:Int,selectedTab: String,status:String): Single<Any>
    fun updateOfflineStorageData(
        tailornaovaDesignId: String?,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfPieces?,
        patternPieces: List<PatternPieceSFCCAPI>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        liningWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        interfaceWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        otherWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        notes: String?
    ): Single<Int>
    fun deleteAndInsert(id:String, patternsData: PatternsData): Single<Any>
    fun getUserData(): Single<Result<LoginUser>>
    fun getPatternDataByID(id:Int):Single<Result<PatternsData>>
    fun getTailernovaDataByID(id: String):Single<Result<OfflinePatternData>>
    fun getWorkspaceDataFromApi(id: String): Single<Result<WorkspaceDataAPI>>
    fun updateWorkspaceDataFromApi(id: String, workspaceDataAPI: WorkspaceDataAPI): Single<Result<WSUpdateResultDomain>>
    fun createWorkspaceDataFromApi(id: String, workspaceDataAPI: WorkspaceDataAPI): Single<Result<WSUpdateResultDomain>>
}