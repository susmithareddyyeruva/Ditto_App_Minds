package com.ditto.workspace.data.mapper

import com.ditto.storage.data.model.OfflinePatternData
import com.ditto.workspace.data.model.*
import com.ditto.workspace.domain.model.*

fun WorkspaceResult.toDomain(): WorkspaceResultDomain {
    return WorkspaceResultDomain(
        version = this.version,
        type = this.type,
        key_property = this.key_property,
        object_type = this.object_type,
        c_traceWorkSpacePattern = this.c_traceWorkSpacePattern.toDomain()
    )
}

fun CTraceWorkSpacePattern.toDomain(): CTraceWorkSpacePatternDomain {
    return CTraceWorkSpacePatternDomain(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfCompletedPieces = this.numberOfCompletedPiece.toDomain(),
        patternPieces = this.patternPieces?.map { it.toDomain() },
        garmetWorkspaceItems = this.garmetWorkspaceItems?.map { it.toDomain() },
        liningWorkspaceItems = this.liningWorkspaceItems?.map { it.toDomain() },
        interfaceWorkspaceItem = this.interfaceWorkspaceItem?.map { it.toDomain() },
    )

}

fun NumberOfCompletedPiece.toDomain(): NumberOfCompletedPieceDomain {
    return NumberOfCompletedPieceDomain(
        garment = this.garment,
        lining = this.lining,
        interfacee = this.interfacee
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
}

fun OfflinePatternData.toDomain(): WorkspaceDataAPI {
    return WorkspaceDataAPI(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfPieces = this.numberOfCompletedPieces.toDomain(),
        garmetWorkspaceItems = this.garmetWorkspaceItemOfflines.map { it.toDomain() },
        liningWorkspaceItems = this.liningWorkspaceItemOfflines.map { it.toDomain() },
        patternPiecesFromApi = this.patternPiecesFromApi.map { it.toDomain() },
        interfaceWorkspaceItems = this.interfaceWorkspaceItemOfflines.map { it.toDomain() }
    )


}


fun NumberOfPieces.toDomain(): com.ditto.storage.data.model.NumberOfCompletedPiecesOffline {
    return com.ditto.storage.data.model.NumberOfCompletedPiecesOffline(
        garment = this.garment,
        lining = this.lining,
        interfacee = this.interfacee
    )
}

fun com.ditto.storage.data.model.NumberOfCompletedPiecesOffline.toDomain(): NumberOfPieces {
    return NumberOfPieces(
        garment = this.garment,
        lining = this.lining,
        interfacee = this.interfacee
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

fun com.ditto.storage.data.model.WorkspaceItemOffline.toDomain(): WorkspaceItemAPIDomain {
    return WorkspaceItemAPIDomain(
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

fun WorkspaceItemAPIDomain.toDomain(): com.ditto.storage.data.model.WorkspaceItemOffline {
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
