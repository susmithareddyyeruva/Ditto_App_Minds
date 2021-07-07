package com.ditto.mylibrary.data.mapper

import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.model.MyLibraryResult
import com.ditto.mylibrary.model.Prod
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
            workspaceItems = it.workspaceItems
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
        workspaceItems = this.workspaceItems
    )
}

fun MyLibraryResult.toDomain(): AllPatternsDomain {
    return AllPatternsDomain(
        action = this.action,
        locale = this.locale,
        prod = this.prod.map { it.toDomain() },
        queryString = this.queryString
    )
}

fun Prod.toDomain(): ProdDomain {
    return ProdDomain(
        ID = this.iD,
        prodBrand = this.prodBrand,
        prodGender = this.prodGender,
        prodName = this.prodName,
        patternType = this.patternType,
        prodSize = this.prodSize,
        tailornovaDesignId = this.tailornovaDesignId,
        tailornovaDesignName = this.tailornovaDesignName,
        image = this.image

    )
}