package com.ditto.workspace.data.model

import com.ditto.storage.data.model.NumberOfPiecesStorage
import com.ditto.storage.data.model.PatternPiecesFromApiWorkspcaeData
import com.ditto.storage.data.model.WorkspaceItemAPI

data class CTraceWorkSpacePatternInputData (
    val tailornaovaDesignId:Int,
    val selectedTab:String,
    val status:String,
    val numberOfCompletedPiece:NumberOfPiecesStorage,
    var patternPieces: List<PatternPiecesFromApiWorkspcaeData> = emptyList(),
    var garmetWorkspaceItems: List<WorkspaceItemAPI> = emptyList(),
    var liningWorkspaceItems: List<WorkspaceItemAPI> = emptyList(),
    var interfaceWorkspaceItem: List<WorkspaceItemAPI> = emptyList()
)