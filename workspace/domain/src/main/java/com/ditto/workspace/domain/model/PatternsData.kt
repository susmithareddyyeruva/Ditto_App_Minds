package com.ditto.workspace.domain.model

data class PatternsData(
    var id: String,
    var patternName: String,
    var description: String,
    var totalPieces: Int,
    var numberOfCompletedPieces:NumberOfCompletedPieceDomain?,
    var completedPieces: Int,
    var totalNumberOfPieces:NumberOfCompletedPieceDomain?,
    var selectedTab: String?,
    var status: String?,
    var thumbnailImagePath: String,
    val thumbnailImageName: String,
    var descriptionImages: List<DescriptionImages> = emptyList(),
    var selvages: List<Selvages> = emptyList(),
    var patternPieces: List<PatternPieces> = emptyList(),
    var liningWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
    var garmetWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
    var interfaceWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList()
)