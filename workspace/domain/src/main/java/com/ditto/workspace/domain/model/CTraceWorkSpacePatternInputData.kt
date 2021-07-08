package com.ditto.workspace.domain.model


data class CTraceWorkSpacePatternInputData (
    val tailornaovaDesignId:Int,
    val selectedTab:String,
    val status:String,
    val numberOfCompletedPiece:NumberOfPieces,
    var patternPieces: List<PatternPieceDomain> = emptyList(),
    var garmetWorkspaceItems: List<WorkspaceItemAPIDomain> = emptyList(),
    var liningWorkspaceItems: List<WorkspaceItemAPIDomain> = emptyList(),
    var interfaceWorkspaceItem: List<WorkspaceItemAPIDomain> = emptyList()
)