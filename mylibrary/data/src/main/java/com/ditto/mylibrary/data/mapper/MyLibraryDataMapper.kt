package com.ditto.mylibrary.data.mapper

import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.storage.data.model.Patterns

internal fun List<Patterns>.toDomain(): List<MyLibraryData> {
    return this.map {
        MyLibraryData(
            id = it.id,
            patternName = it.patternName,
            description = it.description,
            totalPieces = it.totalPieces,
            completedPieces = it.completedPieces,
            status = it.status,
            thumbnailImagePath = it.thumbnailImagePath,
            descriptionImages = it.descriptionImages,
            selvages = it.selvages,
            patternPieces = it.patternPieces,
            garmetWorkspaceItemOfflines = it.garmetWorkspaceItemOfflines,
            liningWorkspaceItemOfflines = it.liningWorkspaceItemOfflines,
            interfaceWorkspaceItemOfflines =it.interfaceWorkspaceItemOfflines
        )
    }
}




internal fun Patterns.toDomain(): MyLibraryData {
    return MyLibraryData(
        id = this.id,
        patternName = this.patternName,
        description = this.description,
        totalPieces = this.totalPieces,
        completedPieces = this.completedPieces,
        status = this.status,
        thumbnailImagePath = this.thumbnailImagePath,
        descriptionImages = this.descriptionImages,
        selvages = this.selvages,
        patternPieces = this.patternPieces,
        garmetWorkspaceItemOfflines = this.garmetWorkspaceItemOfflines,
        liningWorkspaceItemOfflines = this.liningWorkspaceItemOfflines,
        interfaceWorkspaceItemOfflines =this.interfaceWorkspaceItemOfflines
    )
}
