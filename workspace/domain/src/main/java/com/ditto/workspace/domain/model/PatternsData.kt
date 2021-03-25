package com.ditto.workspace.domain.model

data class PatternsData(
    var id: Int = 0,
    var patternName: String,
    var description: String,
    var totalPieces: Int,
    var completedPieces: Int,
    var selectedTab: String,
    var status: String,
    var thumbnailImagePath: String,
    var descriptionImages: List<DescriptionImages> = emptyList(),
    var selvages: List<Selvages> = emptyList(),
    var patternPieces: List<PatternPieces> = emptyList(),
    var workspaceItems: MutableList<WorkspaceItems>? = ArrayList()
)