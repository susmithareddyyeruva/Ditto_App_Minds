package com.ditto.workspace.data.model


import com.google.gson.annotations.SerializedName

data class CTraceWorkSpacePattern(
    @SerializedName("tailornaovaDesignId")
    val tailornaovaDesignId: String? = "",
    @SerializedName("selectedTab")
    val selectedTab: String? = "",
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("numberOfCompletedPiece")
    val numberOfCompletedPiece: NumberOfCompletedPiece?,
    @SerializedName("patternPieces")
    var patternPieces: List<PatternPiece> = emptyList(),
    @SerializedName("garmetWorkspaceItems")
    var garmetWorkspaceItems: List<WorkspaceItem> = emptyList(),
    @SerializedName("liningWorkspaceItems")
    var liningWorkspaceItems: List<WorkspaceItem> = emptyList(),
    @SerializedName("interfaceWorkspaceItems")
    var interfaceWorkspaceItems: List<WorkspaceItem> = emptyList(),
    @SerializedName("otherWorkspaceItems")
    var otherWorkspaceItems: List<WorkspaceItem> = emptyList(),
    @SerializedName("notes")
    val notes: String? = ""
)

