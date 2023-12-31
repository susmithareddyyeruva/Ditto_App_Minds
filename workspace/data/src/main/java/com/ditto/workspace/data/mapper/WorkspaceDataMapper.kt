package com.ditto.workspace.data.mapper

import com.ditto.storage.data.model.*
import com.ditto.workspace.data.model.*
import com.ditto.workspace.domain.model.*


fun CTraceWorkSpacePattern.toDomain(): WorkspaceDataAPI {
    return WorkspaceDataAPI(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfCompletedPiece = this.numberOfCompletedPiece?.toDomain(),
        patternPieces = this.patternPieces.map { it.toDomain() },
        garmetWorkspaceItems = this.garmetWorkspaceItems.map { it.toDomain() }.toMutableList(),
        liningWorkspaceItems = this.liningWorkspaceItems.map { it.toDomain() }.toMutableList(),
        interfaceWorkspaceItems = this.interfaceWorkspaceItems.map { it.toDomain() }.toMutableList(),
        otherWorkspaceItems = this.otherWorkspaceItems.map { it.toDomain() }.toMutableList(),
        notes = this.notes
    )

}

fun NumberOfCompletedPiece.toDomain(): NumberOfPieces {
    return NumberOfPieces(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`,
        other = this.other
    )
}

fun PatternPiece.toDomain(): PatternPieceSFCCAPI {
    return PatternPieceSFCCAPI(
        id = this.id,
        isCompleted = this.isCompleted
    )
}

fun WorkspaceItem.toDomain(): WorkspaceItemDomain {
    return WorkspaceItemDomain(
        id = this.id,
        patternPiecesId = this.patternPiecesId,
        isCompleted = this.isCompleted,
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        transformA = this.transformA,
        transformD = this.transformD,
        rotationAngle = this.rotationAngle,
        isMirrorH = this.isMirrorH,
        isMirrorV = this.isMirrorV,
        showMirrorDialog = this.showMirrorDialog,
        currentSplicedPieceNo = this.currentSplicedPieceNo,
        currentSplicedPieceColumn = this.currentSplicedPieceColumn,
        currentSplicedPieceRow = this.currentSplicedPieceRow
    )
}

fun WSUpdateResult.toDomain(): WSUpdateResultDomain {
    return WSUpdateResultDomain(
        version = this.version,
        type = this.type,
        keyProperty = this.keyProperty,
        resourceState = this.resourceState,
        keyValueString = this.keyValueString,
        objectType = this.objectType,
        cTraceworkspacepattern = this.cTraceworkspacepattern
    )
}
/*
fun WorkspaceDataAPI.toDomain(): OfflinePatternData {
    return OfflinePatternData(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfCompletedPieces = this.numberOfPieces.toDomain(),
        garmetWorkspaceItemOfflines = this.garmetWorkspaceItems.map { it.toDomain() },
        liningWorkspaceItemOfflines = this.liningWorkspaceItems.map { it.toDomain() },
        patternPiecesFromApi = this.patternPiecesFromApi.map { it.toDomain() },
        interfaceWorkspaceItemOfflines = this.interfaceWorkspaceItems.map { it.toDomain() }
    )
}*/

fun OfflinePatterns.toDomain(): WorkspaceDataAPI {
    return WorkspaceDataAPI(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfCompletedPiece = this.numberOfCompletedPieces?.toDomain(),
        patternPieces = this.patternPiecesFromApi.map { it.toDomain() },
        garmetWorkspaceItems = this.garmetWorkspaceItemOfflines.map { it.toDomain() }
            .toMutableList(),
        liningWorkspaceItems = this.liningWorkspaceItemOfflines.map { it.toDomain() }
            .toMutableList(),
        interfaceWorkspaceItems = this.interfaceWorkspaceItemOfflines.map { it.toDomain() }
            .toMutableList(),
        otherWorkspaceItems = this.otherWorkspaceItemOfflines.map { it.toDomain() }
        .toMutableList())
}


fun NumberOfPieces.toDomain(): com.ditto.storage.data.model.NumberOfCompletedPiecesOffline {
    return com.ditto.storage.data.model.NumberOfCompletedPiecesOffline(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`,
        other = this.other
    )
}

fun com.ditto.storage.data.model.NumberOfCompletedPiecesOffline.toDomain(): NumberOfPieces {
    return NumberOfPieces(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`,
        other = this.other
    )
}


fun PatternPieceSFCCAPI.toDomain(): com.ditto.storage.data.model.PatternPiecesOffline {
    return com.ditto.storage.data.model.PatternPiecesOffline(
        id = this.id,
        isCompleted = this.isCompleted
    )
}

fun com.ditto.storage.data.model.PatternPiecesOffline.toDomain(): PatternPieceSFCCAPI {
    return PatternPieceSFCCAPI(
        id = this.id,
        isCompleted = this.isCompleted
    )
}

fun com.ditto.storage.data.model.WorkspaceItemOffline.toDomain(): WorkspaceItemDomain {
    return WorkspaceItemDomain(
        id = this.id,
        patternPiecesId = this.patternPiecesId,
        isCompleted = this.isCompleted,
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        transformA = this.transformA,
        transformD = this.transformD,
        rotationAngle = this.rotationAngle,
        isMirrorH = this.isMirrorH,
        isMirrorV = this.isMirrorV,
        showMirrorDialog = this.showMirrorDialog,
        currentSplicedPieceNo = this.currentSplicedPieceNo,
        currentSplicedPieceRow=this.currentSplicedPieceRow,
        currentSplicedPieceColumn=this.currentSplicedPieceColumn
    )
}

fun WorkspaceItemDomain.toDomain(): com.ditto.storage.data.model.WorkspaceItemOffline {
    return com.ditto.storage.data.model.WorkspaceItemOffline(
        id = this.id,
        patternPiecesId = this.patternPiecesId,
        isCompleted = this.isCompleted,
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        transformA = this.transformA,
        transformD = this.transformD,
        rotationAngle = this.rotationAngle,
        isMirrorH = this.isMirrorH,
        isMirrorV = this.isMirrorV,
        showMirrorDialog = this.showMirrorDialog,
        currentSplicedPieceNo = this.currentSplicedPieceNo,
        currentSplicedPieceColumn=this.currentSplicedPieceColumn,
        currentSplicedPieceRow =this.currentSplicedPieceRow
    )
}

//OfflinePatterns(DB table)>>OfflinePatternData(model)
fun OfflinePatterns.toDomainn(): OfflinePatternData {
    return OfflinePatternData(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfCompletedPieces = this.numberOfCompletedPieces?.toDomainOfflinePicecs(),
        patternPiecesFromApi = this.patternPiecesFromApi.map { it.toDomain() },
        garmetWorkspaceItemOfflines = this.garmetWorkspaceItemOfflines.map { it.toDomain1() }
            .toMutableList(),
        liningWorkspaceItemOfflines = this.liningWorkspaceItemOfflines.map { it.toDomain1() }
            .toMutableList(),
        interfaceWorkspaceItemOfflines = this.interfaceWorkspaceItemOfflines.map { it.toDomain1() }
            .toMutableList(),
        otherWorkspaceItemOfflines = this.otherWorkspaceItemOfflines.map { it.toDomain1() }
            .toMutableList(),
        id = this.designId,
        patternName = this.patternName,
        description = this.description,
        patternType = this.patternType,
        numberOfPieces = this.numberOfPieces?.toDomainOfflinePicecs(),
        orderModificationDate = this.orderModificationDate,
        orderCreationDate = this.orderCreationDate,
        instructionFileName = this.instructionFileName,
        instructionUrl = this.instructionUrl,
        thumbnailImageUrl = this.thumbnailImageUrl,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailEnlargedImageName = this.thumbnailEnlargedImageName,
        patternDescriptionImageUrl = this.patternDescriptionImageUrl,
        selvages = this.selvages?.map { it.toDomainStorage() },
        patternPiecesTailornova = this.patternPiecesFromTailornova?.map { it.toDomainn() },
        brand = this.brand,
        size = this.size,
        gender = this.gender,
        customization = this.customization,
        dressType = this.dressType,
        suitableFor = this.suitableFor,
        occasion = this.occasion,
        selectedMannequinId = this.selectedMannequinId,
        selectedMannequinName = this.selectedMannequinName,
        notes = this.notes
    )
}

fun PatternPieceDataDomain.toDomainn(): PatternPieceData {
    return PatternPieceData(
        cutOnFold = this.cutOnFold,
        cutQuantity = this.cutQuantity,
        pieceDescription = this.pieceDescription,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        thumbnailImageName=this.thumbnailImageName,
        thumbnailImageUrl=this.thumbnailImageUrl,
        isSpliced = this.isSpliced,
        pieceNumber = this.pieceNumber,
        positionInTab = this.positionInTab,
        size = this.size,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        tabCategory = this.tabCategory,
        contrast = this.contrast
    )
}

fun PatternPieceData.toDomainn(): PatternPieceDataDomain {
    return PatternPieceDataDomain(
        cutOnFold = this.cutOnFold,
        cutQuantity = this.cutQuantity,
        pieceDescription = this.pieceDescription,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        thumbnailImageName=this.thumbnailImageName,
        thumbnailImageUrl=this.thumbnailImageUrl,
        isSpliced = this.isSpliced,
        pieceNumber = this.pieceNumber,
        positionInTab = this.positionInTab,
        size = this.size,
        view=this.view,
        isMirrorOption=this.isMirrorOption,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        tabCategory = this.tabCategory,
        contrast = this.contrast
    )
}

fun SplicedImageData.toDomain(): SplicedImageDomain {
    return SplicedImageDomain(
        column = this.column,
        designId = this.designId,
        id = this.id,
        imageName = this.imageName,
        imageUrl
        = this.imageUrl,
        mapImageName = this.mapImageName,
        mapImageUrl = this.mapImageUrl,
        pieceId = this.pieceId,
        row = this.row
    )
}
fun SplicedImageDomain.toDomain(): SplicedImageData {
    return SplicedImageData(
        column = this.column,
        designId = this.designId,
        id = this.id,
        imageName = this.imageName,
        imageUrl
        = this.imageUrl,
        mapImageName = this.mapImageName,
        mapImageUrl = this.mapImageUrl,
        pieceId = this.pieceId,
        row = this.row
    )
}

fun SelvageData.toDomainStorage(): SelvageDomain {
    return SelvageDomain(
        fabricLength = this.fabricLength,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        tabCategory = this.tabCategory
    )
}

fun WorkspaceItemOffline.toDomain1(): WorkspaceItemOfflineDomain {
    return WorkspaceItemOfflineDomain(
        id = this.id,
        patternPiecesId = this.patternPiecesId,
        isCompleted = this.isCompleted,
        xcoordinate = this.xcoordinate,
        ycoordinate = this.ycoordinate,
        pivotX = this.pivotX,
        pivotY = this.pivotY,
        transformA = this.transformA,
        transformD = this.transformD,
        rotationAngle = this.rotationAngle,
        isMirrorV = this.isMirrorV,
        isMirrorH = this.isMirrorH,
        showMirrorDialog = this.showMirrorDialog,
        currentSplicedPieceNo = this.currentSplicedPieceNo,
        currentSplicedPieceRow = this.currentSplicedPieceRow,
        currentSplicedPieceColumn = this.currentSplicedPieceColumn
    )
}

fun NumberOfCompletedPiecesOffline.toDomainOfflinePicecs(): NumberOfPieces {
    return NumberOfPieces(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`,
        other = this.other
    )
}


