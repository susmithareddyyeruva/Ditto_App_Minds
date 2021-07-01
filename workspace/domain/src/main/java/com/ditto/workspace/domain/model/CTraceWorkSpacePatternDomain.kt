package com.ditto.workspace.domain.model

data class CTraceWorkSpacePatternDomain (
    val tailornaovaDesignId:String?,
    val selectedTab:String?,
    val status:String?,
    val numberOfCompletedPieces:NumberOfCompletedPieceDomain,
    var patternPieces: List<PatternPieceDomain> = emptyList(),
    var garmetWorkspaceItems: List<WorkspaceItemDomain> = emptyList(),
    var liningWorkspaceItems: List<WorkspaceItemDomain> = emptyList(),
    var interfaceWorkspaceItem: List<WorkspaceItemDomain> = emptyList()
)
