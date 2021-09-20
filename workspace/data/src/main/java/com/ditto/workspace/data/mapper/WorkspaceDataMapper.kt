package com.ditto.workspace.data.mapper

import com.ditto.storage.data.model.*
import com.ditto.workspace.data.model.*
import com.ditto.workspace.domain.model.*

fun WorkspaceResult.toDomain(): WorkspaceResultDomain {
    return WorkspaceResultDomain(
        version = this.version,
        type = this.type,
        key_property = this.key_property,
        object_type = this.object_type,
        c_traceWorkSpacePattern = this.c_traceWorkSpacePattern?.toDomain()
    )
}

fun CTraceWorkSpacePattern.toDomain(): CTraceWorkSpacePatternDomain {
    return CTraceWorkSpacePatternDomain(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfCompletedPieces = this.numberOfCompletedPiece?.toDomain(),
        patternPieces = this.patternPieces?.map { it.toDomain() },
        garmetWorkspaceItems = this.garmetWorkspaceItems?.map { it.toDomain() },
        liningWorkspaceItems = this.liningWorkspaceItems?.map { it.toDomain() },
        interfaceWorkspaceItems = this.interfaceWorkspaceItems?.map { it.toDomain() },
    )

}

fun NumberOfCompletedPiece.toDomain(): NumberOfPieces {
    return NumberOfPieces(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`
    )
}

fun PatternPiece.toDomain(): PatternPieceDomain {
    return PatternPieceDomain(
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
        currentSplicedPieceNo = this.currentSplicedPieceNo
    )
}

fun WSUpdateResult.toDomain(): WSUpdateResultDomain {
    return WSUpdateResultDomain(
        version = this.version,
        type = this.type,
        key_property = this.key_property,
        resource_state = this.resource_state,
        key_value_string = this.key_value_string,
        object_type = this.object_type,
        c_traceWorkSpacePattern = this.c_traceWorkSpacePattern
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
        numberOfPieces = this.numberOfCompletedPieces.toDomain(),
        patternPiecesFromApi = this.patternPiecesFromApi.map { it.toDomain() },
        garmetWorkspaceItems = this.garmetWorkspaceItemOfflines.map { it.toDomain() }.toMutableList(),
        liningWorkspaceItems = this.liningWorkspaceItemOfflines.map { it.toDomain() }.toMutableList(),
        interfaceWorkspaceItems = this.interfaceWorkspaceItemOfflines.map { it.toDomain() }.toMutableList()
    )
}


fun NumberOfPieces.toDomain(): com.ditto.storage.data.model.NumberOfCompletedPiecesOffline {
    return com.ditto.storage.data.model.NumberOfCompletedPiecesOffline(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`
    )
}

fun com.ditto.storage.data.model.NumberOfCompletedPiecesOffline.toDomain(): NumberOfPieces {
    return NumberOfPieces(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`
    )
}


fun PatternPieceDomain.toDomain(): com.ditto.storage.data.model.PatternPiecesOffline {
    return com.ditto.storage.data.model.PatternPiecesOffline(
        id = this.id,
        isCompleted = this.isCompleted
    )
}

fun com.ditto.storage.data.model.PatternPiecesOffline.toDomain(): PatternPieceDomain {
    return PatternPieceDomain(
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
    )
}

//OfflinePatterns(DB table)>>OfflinePatternData(model)
fun OfflinePatterns.toDomainn(): OfflinePatternData {
    return OfflinePatternData(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfCompletedPieces = this.numberOfCompletedPieces.toDomainOfflinePicecs(),
        patternPiecesFromApi = this.patternPiecesFromApi.map { it.toDomain1() },
        garmetWorkspaceItemOfflines = this.garmetWorkspaceItemOfflines?.map { it.toDomain1() }
            .toMutableList(),
        liningWorkspaceItemOfflines = this.liningWorkspaceItemOfflines?.map { it.toDomain1() }
            .toMutableList(),
        interfaceWorkspaceItemOfflines = this.interfaceWorkspaceItemOfflines?.map { it.toDomain1() }
            ?.toMutableList(),
        id = this.id,
        name = this.name,
        description = this.description,
        patternType = this.patternType,
        numberOfPieces = this.numberOfPieces.toDomainOfflinePicecs(),
        orderModificationDate = this.orderModificationDate,
        orderCreationDate = this.orderCreationDate,
        instructionFileName = this.instructionFileName,
        instructionUrl = this.instructionUrl,
        thumbnailImageUrl = this.thumbnailImageUrl,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailEnlargedImageName = this.thumbnailEnlargedImageName,
        patternDescriptionImageUrl = this.patternDescriptionImageUrl,
        selvages = this.selvages.map { it.toDomainStorage() },
        patternPieces = this.patternPieces.map { it.toDomainn() },
        brand = this.brand,
        size = this.size,
        gender = this.gender,
        customization = this.customization,
        dressType = this.dressType,
        suitableFor = this.suitableFor,
        occasion = this.occasion
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
        spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages.map { it.toDomain() },
        tabCategory = this.tabCategory
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
        spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages.map { it.toDomain() },
        tabCategory = this.tabCategory
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
        `interface` = this.`interface`
    )
}

fun PatternPiecesOffline.toDomain1(): PatternPiecesOfflineDomain {
    return PatternPiecesOfflineDomain(
        id = this.id,
        isCompleted = this.isCompleted
    )
}
