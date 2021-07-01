package com.ditto.workspace.data.model


import com.google.gson.annotations.SerializedName

data class CTraceWorkSpacePattern (
    @SerializedName("tailornaovaDesignId")
    val tailornaovaDesignId:String?,
    @SerializedName("selectedTab")
    val selectedTab:String?,
    @SerializedName("status")
    val status:String?,
    @SerializedName("numberOfCompletedPieces")
    val numberOfCompletedPiece:NumberOfCompletedPiece,
    @SerializedName("patternPieces")
    var patternPieces: List<PatternPiece> = emptyList(),
    @SerializedName("garmetWorkspaceItems")
    var garmetWorkspaceItems: List<WorkspaceItem> = emptyList(),
    @SerializedName("liningWorkspaceItems")
    var liningWorkspaceItems: List<WorkspaceItem> = emptyList(),
    @SerializedName("interfaceWorkspaceItems")
    var interfaceWorkspaceItem: List<WorkspaceItem> = emptyList()
)

