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
        selectedTab: String,
        status: String,
        numberOfCompletedPiece: NumberOfPieces,
        patternPieces: List<PatternPieceDomain>,
        garmetWorkspaceItems: List<WorkspaceItemDomain>,
        liningWorkspaceItems: List<WorkspaceItemDomain>,
        interfaceWorkspaceItem: List<WorkspaceItemDomain>
    ): Single<Any>
    fun insertWorkspaceData(w: WorkspaceDataAPI): Single<Any>//follow same
    fun getPatternDataByID(id: Int):Single<Result<PatternsData>>
    fun getTailernovaDataByID(id: String):Single<Result<OfflinePatternData>>
    fun getUserDetails():Single<Result<LoginUser>>
    fun deleteAndInsert(id: String, patternsData: PatternsData): Single<Any>
    fun getWorkspaceData() : Single<Result<CTraceWorkSpacePatternDomain>>
    fun updateWorkspaceData(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData) : Single<Result<WSUpdateResultDomain>>
    fun createWorkspaceData(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData) : Single<Result<WSUpdateResultDomain>>
}

