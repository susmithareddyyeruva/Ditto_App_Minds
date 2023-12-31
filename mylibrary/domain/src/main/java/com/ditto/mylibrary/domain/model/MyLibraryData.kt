 package com.ditto.mylibrary.domain.model

import com.ditto.storage.data.model.DescriptionImages
import com.ditto.storage.data.model.PatternPieces
import com.ditto.storage.data.model.Selvages
import com.ditto.storage.data.model.WorkspaceItems

data class MyLibraryData(
    val id: String,
    var patternName: String?,
    val description: String?,
    val totalPieces: Int,
    val completedPieces: Int,
    val status: String?,
    var thumbnailImagePath: String?,
    var thumbnailImageName: String?,
    var descriptionImages: List<DescriptionImages>,
    var selvages: List<Selvages>?= emptyList(),
    var patternPieces: List<PatternPieces>?= emptyList(),
   // var workspaceItems: MutableList<WorkspaceItems>? = ArrayList()
    var interfaceWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
    var garmetWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList(),
    var liningWorkspaceItemOfflines: MutableList<WorkspaceItems>? = ArrayList()
)