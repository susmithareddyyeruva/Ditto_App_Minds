package com.ditto.workspace.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.workspace.domain.model.*
import io.reactivex.Single
import non_core.lib.Result

interface GetWorkspaceData {
    fun invoke(): Single<Result<List<PatternsData>>>
    fun insert(patternsData: PatternsData): Single<Any>//follow same
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
    fun insertWorkspaceData(w: WorkspaceDataAPI): Single<Any>//follow same
    fun getPatternDataByID(id: Int):Single<Result<PatternsData>>
    fun getTailernovaDataByID(id: String):Single<Result<OfflinePatternData>>
    fun getUserDetails():Single<Result<LoginUser>>
    fun deleteAndInsert(id: String, patternsData: PatternsData): Single<Any>
    fun getWorkspaceData(id: String): Single<Result<CTraceWorkSpacePatternDomain>>
    fun updateWorkspaceData(id: String,cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData) : Single<Result<WSUpdateResultDomain>>
    fun createWorkspaceData(id: String,cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData) : Single<Result<WSUpdateResultDomain>>
}

