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
        tailornaovaDesignId: Int,
        selectedTab: String,
        status: String,
        numberOfCompletedPiece: NumberOfPieces,
        patternPieces: List<PatternPieceDomain>,
        garmetWorkspaceItems: List<WorkspaceItemAPIDomain>,
        liningWorkspaceItems: List<WorkspaceItemAPIDomain>,
        interfaceWorkspaceItem: List<WorkspaceItemAPIDomain>
    ): Single<Any>
    fun deleteAndInsert(id:Int, patternsData: PatternsData): Single<Any>
    fun getUserData(): Single<Result<LoginUser>>
    fun getWorkspaceDataFromApi(): Single<Result<WorkspaceResultDomain>>
    fun updateWorkspaceDataFromApi(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData): Single<Result<WSUpdateResultDomain>>
}