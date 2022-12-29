package com.ditto.workspace.domain.model

data class PatternsData(
    var id: String,
    var patternName: String?,
    var patternType: String?,
    var description: String?,
    var totalPieces: Int,
    var numberOfCompletedPiece:NumberOfPieces?,
    var completedPieces: Int,
    var totalNumberOfPieces:NumberOfPieces?,
    var selectedTab: String?,
    var status: String?,
    var thumbnailImagePath: String?,
    val thumbnailImageName: String?,
    val instructionUrl: String?,
    var descriptionImages: List<DescriptionImages> = emptyList(),
    var selvages: List<Selvages>? = emptyList(),
    var patternPieces: List<PatternPieces>? = emptyList(),
    var liningWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
    var garmetWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
    var interfaceWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
    var otherWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList()

)