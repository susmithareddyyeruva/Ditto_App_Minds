package com.ditto.workspace.domain.model


data class DragData(
    var type :Int = 0,
    var id: Int = 0,
    var width: Int = 0,
    var height: Int = 0,
    var patternPieces: PatternPieces?,
    var workspaceItems: WorkspaceItems?
)