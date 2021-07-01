package com.ditto.workspace.data.model

data class CTraceWorkSpacePatternInputData (
    val tailornaovaDesignId:String?,
    val selectedTab:String?,
    val status:String?,
    val numberOfCompletedPiece:NumberOfCompletedPieceInputData,
    var patternPieces: List<PatternPieceInputData> = emptyList(),
    var garmetWorkspaceItems: List<WorkspaceItemInputData> = emptyList(),
    var liningWorkspaceItems: List<WorkspaceItemInputData> = emptyList(),
    var interfaceWorkspaceItem: List<WorkspaceItemInputData> = emptyList()
)