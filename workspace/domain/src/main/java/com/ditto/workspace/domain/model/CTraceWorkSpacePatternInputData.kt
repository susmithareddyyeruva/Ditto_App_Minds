package com.ditto.workspace.domain.model


data class CTraceWorkSpacePatternInputData (
    val tailornaovaDesignId:String,
    val selectedTab:String,
    val status:String,
    val numberOfCompletedPiece:NumberOfPieces?,
    var patternPieces: List<PatternPieceDomain> = emptyList(),
    var garmetWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList(),
    var liningWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList(),
    var interfaceWorkspaceItem: MutableList<WorkspaceItemDomain>? = ArrayList()
)