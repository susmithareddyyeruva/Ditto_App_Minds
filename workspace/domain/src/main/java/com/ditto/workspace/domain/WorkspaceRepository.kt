package com.ditto.workspace.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.model.*
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface WorkspaceRepository {
    fun getWorkspaceData(): Single<Result<List<PatternsData>>>
    fun insertData(patternsData: PatternsData): Single<Any>
    fun insertWorkspaceData(patternsData: WorkspaceDataAPI): Single<Any>
    //fun updateOfflineStorageData(tailornaovaDesignId:Int,selectedTab: String,status:String): Single<Any>
    fun updateOfflineStorageData(
        tailornaovaDesignId: String,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfPieces?,
        patternPieces: List<PatternPieceDomain>,
        garmetWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        liningWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        interfaceWorkspaceItem: MutableList<WorkspaceItemDomain>?
    ): Single<Any>
    fun deleteAndInsert(id:String, patternsData: PatternsData): Single<Any>
    fun getUserData(): Single<Result<LoginUser>>
    fun getPatternDataByID(id:Int):Single<Result<PatternsData>>
    fun getTailernovaDataByID(id: String):Single<Result<OfflinePatternData>>
    fun getWorkspaceDataFromApi(id: String): Single<Result<CTraceWorkSpacePatternDomain>>
    fun updateWorkspaceDataFromApi(id: String,cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData): Single<Result<WSUpdateResultDomain>>
    fun createWorkspaceDataFromApi(id: String,cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData): Single<Result<WSUpdateResultDomain>>
}