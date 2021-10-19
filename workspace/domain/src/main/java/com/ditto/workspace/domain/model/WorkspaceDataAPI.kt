package com.ditto.workspace.domain.model


data class WorkspaceDataAPI (
    val tailornaovaDesignId:String? = "",
    val selectedTab:String? = "",
    val status:String? = "",
    val numberOfCompletedPiece:NumberOfPieces?,
    var patternPieces: List<PatternPieceSFCCAPI>? = emptyList(),
    var garmetWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList(),
    var liningWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList(),
    var interfaceWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList()
)