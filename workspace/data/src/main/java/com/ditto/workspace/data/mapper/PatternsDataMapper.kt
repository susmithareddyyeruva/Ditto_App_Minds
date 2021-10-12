package com.ditto.workspace.data.mapper

import com.ditto.storage.data.model.*
import com.ditto.workspace.domain.model.NumberOfPieces
import com.ditto.workspace.domain.model.PatternsData
import com.ditto.workspace.domain.model.Selvages

internal fun List<Patterns>.toDomain(): List<PatternsData>
{
    return this.map {
        PatternsData(
            id = it.id,
            patternName = it.patternName,
            description = it.description,
            totalPieces = it.totalPieces,
            completedPieces = it.completedPieces,
            selectedTab = it.selectedTab,
            status = it.status,
            thumbnailImagePath = it.thumbnailImagePath,
            thumbnailImageName =it.thumbnailImageName,
            numberOfCompletedPiece = it.numberOfCompletedPieces?.toDomainn(),
            totalNumberOfPieces =it.numberOfPieces?.toDomainn(),
            descriptionImages = it.descriptionImages.map { it.toDomain() },
            selvages = it.selvages?.map { it.toDomain() },
            patternPieces = it.patternPieces?.map { it.toDomain() },
            liningWorkspaceItemOfflines = it.liningWorkspaceItemOfflines?.map { it.toDomain() }
                ?.toMutableList(),
            garmetWorkspaceItemOfflines = it.garmetWorkspaceItemOfflines?.map { it.toDomain() }
                ?.toMutableList(),
            interfaceWorkspaceItemOfflines = it.interfaceWorkspaceItemOfflines?.map { it.toDomain() }
                ?.toMutableList()
        )
    }
}
internal fun Patterns.toDomain(): PatternsData {
    return PatternsData(
        id = this.id,
        patternName = this.patternName,
        description = this.description,
        totalPieces = this.totalPieces,
        completedPieces = this.completedPieces,
        selectedTab = this.selectedTab,
        status = this.status,
        thumbnailImagePath = this.thumbnailImagePath,
        thumbnailImageName =this.thumbnailImageName,
        numberOfCompletedPiece = this.numberOfCompletedPieces?.toDomainn(),
        totalNumberOfPieces =this.numberOfPieces?.toDomainn(),
        descriptionImages = this.descriptionImages.map { it.toDomain() },
        selvages = this.selvages?.map { it.toDomain() },
        patternPieces = this.patternPieces?.map { it.toDomain() },
        garmetWorkspaceItemOfflines = this.garmetWorkspaceItemOfflines?.map { it.toDomain() }?.toMutableList(),
        liningWorkspaceItemOfflines = this.liningWorkspaceItemOfflines?.map { it.toDomain() }?.toMutableList(),
        interfaceWorkspaceItemOfflines = this.interfaceWorkspaceItemOfflines?.map { it.toDomain() }?.toMutableList()
    )
}

internal fun DescriptionImages.toDomain(): com.ditto.workspace.domain.model.DescriptionImages {
    return com.ditto.workspace.domain.model.DescriptionImages(
        id = this.id,
        imagePath = this.imagePath
    )
}

internal fun SpliceImages.toDomain(): com.ditto.workspace.domain.model.SpliceImages {
    return com.ditto.workspace.domain.model.SpliceImages(
        id = this.id,
        row = this.row,
        column = this.column,
        reference_splice = this.reference_splice,
        imagePath = this.imagePath,
        imageName = this.imagePath,
        mapImageUrl = this.mapImageUrl,
        mapImageName = this.mapImageName
    )
}

internal fun com.ditto.storage.data.model.Selvages.toDomain(): Selvages {
    return Selvages(
        id = this.id,
        imagePath = this.imagePath,
        tabCategory = this.tabCategory,
        fabricLength = this.fabricLength
    )
}


internal fun PatternPieces.toDomain(): com.ditto.workspace.domain.model.PatternPieces {
    return com.ditto.workspace.domain.model.PatternPieces(
        id = this.id,
        parentPattern = this.parentPattern,
        imagePath = this.imagePath,
        imageName=this.imageName,
        thumbnailImageUrl= this.thumbnailImageUrl,
        thumbnailImageName=this.thumbnailImageName,
        size = this.size,
        view = this.view,
        pieceNumber = this.pieceNumber,
        pieceDescription = this.pieceDescription,
        positionInTab = this.positionInTab,
        tabCategory = this.tabCategory,
        cutQuantity = this.cutQuantity,
        splice = this.splice,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        cutOnFold = this.cutOnFold,
        isMirrorOption = this.mirrorOption,
        isCompleted = this.isCompleted
    )

}

internal fun WorkspaceItems.toDomain(): com.ditto.workspace.domain.model.WorkspaceItems {
    return com.ditto.workspace.domain.model.WorkspaceItems(
        id = this.id,
        parentPattern = this.parentPattern,
        imagePath = this.imagePath,
        size = this.size,
        view = this.view,
        pieceNumber = this.pieceNumber,
        pieceDescription = this.pieceDescription,
        positionInTab = this.positionInTab,
        tabCategory = this.tabCategory,
        cutQuantity = this.cutQuantity,
        splice = this.splice,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        cutOnFold = this.cutOnFold,
        mirrorOption = this.mirrorOption,
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        rotationAngle = this.rotationAngle,
        isMirrorH = this.isMirrorH,
        isMirrorV = this.isMirrorV,
        parentPatternId = this.parentPatternId,
        isCompleted = this.isCompleted,
        currentSplicedPieceRow = this.currentSplicedPieceRow,
        currentSplicedPieceColumn = this.currentSplicedPieceColumn
    )
}
//todo check below
internal fun PatternsData.toDomain(): Patterns {
    return Patterns(
        id = this.id,
        patternName = this.patternName,
        description = this.description,
        totalPieces = this.totalPieces,
        completedPieces = this.completedPieces,
        selectedTab = this.selectedTab,
        status = this.status,
        thumbnailImagePath = this.thumbnailImagePath,
        numberOfCompletedPieces= this.numberOfCompletedPiece?.toDomain(),
        numberOfPieces= this.totalNumberOfPieces?.toDomain(),
        descriptionImages = this.descriptionImages.map { it.toDomain() },
        selvages = this.selvages?.map { it.toDomain() },
        patternPieces = this.patternPieces?.map { it.toDomain() },
        garmetWorkspaceItemOfflines = this.garmetWorkspaceItemOfflines?.map { it.toDomain() }?.toMutableList(),
        liningWorkspaceItemOfflines = this.liningWorkspaceItemOfflines?.map { it.toDomain() }?.toMutableList(),
        interfaceWorkspaceItemOfflines= this.interfaceWorkspaceItemOfflines?.map { it.toDomain() }?.toMutableList()
    )
}

fun NumberOfCompletedPiecesOffline.toDomainn(): NumberOfPieces {
    return NumberOfPieces(
        garment=this.garment,
        lining=this.lining,
        `interface` = this.`interface`
    )
}

internal fun com.ditto.workspace.domain.model.DescriptionImages.toDomain(): DescriptionImages {
    return DescriptionImages(
        id = this.id,
        imagePath = this.imagePath
    )
}

internal fun com.ditto.workspace.domain.model.SpliceImages.toDomain(): SpliceImages {
    return SpliceImages(
        id = this.id,
        row = this.row,
        column = this.column,
        reference_splice = this.reference_splice,
        imagePath = this.imagePath
    )
}

internal fun Selvages.toDomain(): com.ditto.storage.data.model.Selvages {
    return com.ditto.storage.data.model.Selvages(
        id = this.id,
        imagePath = this.imagePath,
        tabCategory = this.tabCategory,
        fabricLength = this.fabricLength
    )
}


internal fun com.ditto.workspace.domain.model.PatternPieces.toDomain(): PatternPieces {
    return PatternPieces(
        id = this.id,
        parentPattern = this.parentPattern,
        imagePath = this.imagePath,
        size = this.size,
        view = this.view,
        pieceNumber = this.pieceNumber,
        pieceDescription = this.pieceDescription,
        positionInTab = this.positionInTab,
        tabCategory = this.tabCategory,
        cutQuantity = this.cutQuantity,
        splice = this.splice,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        cutOnFold = this.cutOnFold,
        mirrorOption = this.isMirrorOption,
        isCompleted = this.isCompleted
    )

}

internal fun com.ditto.workspace.domain.model.WorkspaceItems.toDomain(): WorkspaceItems {
    return WorkspaceItems(
        id = this.id,
        parentPattern = this.parentPattern,
        imagePath = this.imagePath,
        size = this.size,
        view = this.view,
        pieceNumber = this.pieceNumber,
        pieceDescription = this.pieceDescription,
        positionInTab = this.positionInTab,
        tabCategory = this.tabCategory,
        cutQuantity = this.cutQuantity,
        splice = this.splice,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        cutOnFold = this.cutOnFold,
        mirrorOption = this.mirrorOption,
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        rotationAngle = this.rotationAngle,
        isMirrorH = this.isMirrorH,
        isMirrorV = this.isMirrorV,
        parentPatternId = this.parentPatternId,
        isCompleted = this.isCompleted,
        currentSplicedPieceRow = this.currentSplicedPieceRow,
        currentSplicedPieceColumn = this.currentSplicedPieceColumn
    )
}