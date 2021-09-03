package com.ditto.workspace.data.mapper

import com.ditto.workspace.domain.model.*


fun NumberOfCompletedPieceDomain.toDomain(): NumberOfPieces {
    return NumberOfPieces(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`
    )
}

fun PatternPieces.toPatternPieceDomain(): PatternPieceDomain {
    return PatternPieceDomain(
        id = this.id,
        isCompleted = this.isCompleted
    )
}

fun WorkspaceItems.toWorkspaceItemDomain(): WorkspaceItemDomain {
    return WorkspaceItemDomain(
        id = this.id,
        patternPiecesId = this.parentPatternId,
        isCompleted = this.isCompleted,
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        transformA = "transformA", // todo check need IOS stuff
        transformD = "transformD", // todo check need IOS stuff
        rotationAngle = this.rotationAngle,
        isMirrorH = this.isMirrorH,
        isMirrorV = this.isMirrorV,
        showMirrorDialog = this.showMirrorDialog,
        currentSplicedPieceColumn=this.currentSplicedPieceColumn,
        currentSplicedPieceRow=this.currentSplicedPieceRow,
        currentSplicedPieceNo = "currentSplicedPieceNo" //todo check >> add row and column
    )
}


fun SelvageDomain.toOldModel(): Selvages {
    return Selvages(
        id = this.id,
        imageName = this.imageName,
        imagePath = this.imageUrl,
        fabricLength = this.fabricLength,
        tabCategory = this.tabCategory
    )
}

fun SplicedImageDomain.toOldModel(): SpliceImages {
    return SpliceImages(
        id = this.id,
        row = this.row,
        column = this.column,
        reference_splice = this.mapImageName,
        imagePath = this.imageUrl,
        imageName = this.imageName,
    )
}

fun PatternPieceDataDomain.toOldModel(): PatternPieces {
    return PatternPieces(
        id = this.id,
        parentPattern = "parentPattern", // todo
        imagePath = this.imageUrl,
        size = this.size,
        view = this.view,
        pieceNumber = this.pieceNumber,
        pieceDescription = this.pieceDescription,
        positionInTab = this.positionInTab,
        tabCategory = this.tabCategory,
        cutQuantity = this.cutQuantity,
        splice = this.isSpliced,
        spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages.map {
            it.toOldModel()
        },
        cutOnFold = this.cutOnFold.toString(),
        mirrorOption = true,//Todo mirror
        isCompleted = false // TODO
    )
}

fun SplicedImageDomain.toOldModelSpliceImage(): SpliceImages {
    return SpliceImages(
        id = this.id,
        row = this.row,
        column = this.column,
        reference_splice = this.mapImageName,
        imageName = this.imageName,
        imagePath = this.imageUrl
    )
}



fun WorkspaceItemDomain.toOldModel(patternPieces: List<PatternPieceDataDomain>): WorkspaceItems {

    val patternPiece = getSpliedImges(this.patternPiecesId, patternPieces)

    return WorkspaceItems(
        id = this.id,
        parentPattern = "parentPattern",// todo check
        imagePath = patternPiece?.imageUrl,
        size = patternPiece?.size,
        view = patternPiece?.view,
        pieceNumber = patternPiece?.pieceNumber,
        pieceDescription = patternPiece?.pieceDescription,
        positionInTab = patternPiece?.positionInTab,
        tabCategory = patternPiece?.tabCategory,
        cutQuantity = patternPiece?.cutQuantity ?: "", // todo check I used !!
        splice = patternPiece?.isSpliced ?: false,
        spliceDirection = patternPiece?.spliceDirection,
        spliceScreenQuantity = patternPiece?.spliceScreenQuantity,
        cutOnFold = patternPiece?.cutOnFold.toString(),
        mirrorOption = false,//todo check should come from tailornova
        splicedImages = patternPiece?.splicedImages?.map { it.toOldModelSpliceImage() },
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        rotationAngle = this.rotationAngle,
        isMirrorV = this.isMirrorV,
        isMirrorH = this.isMirrorH,
        showMirrorDialog = this.showMirrorDialog,
        parentPatternId = this.patternPiecesId,
        isCompleted = this.isCompleted,
        currentSplicedPieceRow = this.currentSplicedPieceRow,
        currentSplicedPieceColumn = this.currentSplicedPieceColumn
    )
}

private fun getSpliedImges(
    patternPiecesId: Int?,
    patternPieces: List<PatternPieceDataDomain>
): PatternPieceDataDomain? {
    return patternPieces.find { it.id == patternPiecesId }
}

