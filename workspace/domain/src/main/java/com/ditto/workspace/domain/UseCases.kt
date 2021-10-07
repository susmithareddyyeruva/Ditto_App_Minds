package com.ditto.workspace.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.model.*
import io.reactivex.Single
import non_core.lib.Result

interface GetWorkspaceData {
    fun insert(patternsData: PatternsData): Single<Any>//follow same
    fun updateOfflineStorageData(
        tailornaovaDesignId: String,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfPieces?,
        patternPieces: List<PatternPieceSFCCAPI>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        liningWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        interfaceWorkspaceItems: MutableList<WorkspaceItemDomain>?
    ): Single<Int>
    fun insertWorkspaceData(w: WorkspaceDataAPI): Single<Any>//follow same
    fun getPatternDataByID(id: Int):Single<Result<PatternsData>>
    fun getTailernovaDataByID(id: String):Single<Result<OfflinePatternData>>
    fun getUserDetails():Single<Result<LoginUser>>
    fun deleteAndInsert(id: String, patternsData: PatternsData): Single<Any>
    fun getWorkspaceData(id: String): Single<Result<WorkspaceDataAPI>>
    fun updateWorkspaceData(id: String, workspaceDataAPI: WorkspaceDataAPI) : Single<Result<WSUpdateResultDomain>>
    fun createWorkspaceData(id: String, workspaceDataAPI: WorkspaceDataAPI) : Single<Result<WSUpdateResultDomain>>
}

