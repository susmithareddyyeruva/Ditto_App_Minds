package com.ditto.workspace.data.mapper

import com.ditto.workspace.data.model.*
import com.ditto.workspace.domain.model.*

fun WorkspaceResult.toDomain(): WorkspaceResultDomain{
return WorkspaceResultDomain(
    version = this.version,
    type =  this.type,
    key_property = this.key_property,
    object_type = this.object_type,
    c_traceWorkSpacePattern = this.c_traceWorkSpacePattern.toDomain()
)
}

fun CTraceWorkSpacePattern.toDomain(): CTraceWorkSpacePatternDomain {
    return CTraceWorkSpacePatternDomain(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab =  this.selectedTab,
        status =  this.status,
        numberOfCompletedPieces = this.numberOfCompletedPiece.toDomain(),
        patternPieces = this.patternPieces?.map { it.toDomain() },
        garmetWorkspaceItems = this.garmetWorkspaceItems?.map { it.toDomain() },
        liningWorkspaceItems = this.liningWorkspaceItems?.map{it.toDomain()},
    )

}

fun NumberOfCompletedPiece.toDomain(): NumberOfCompletedPieceDomain {
    return NumberOfCompletedPieceDomain(
        garment = this.garment,
        lining = this.lining,
        interfacee = this.interfacee
    )
}

fun PatternPiece.toDomain(): PatternPieceDomain{
    return PatternPieceDomain(
        id = this.id,
        isCompleted = this.isCompleted
    )
}

fun WorkspaceItem.toDomain():WorkspaceItemDomain{
    return WorkspaceItemDomain(
        id=this.id,
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
        isMirrorV =this.isMirrorV,
        showMirrorDialog = this.showMirrorDialog,
        currentSplicedPieceNo = this.currentSplicedPieceNo
    )
}

fun WSUpdateResult.toDomain():WSUpdateResultDomain{
    return WSUpdateResultDomain(
        version=this.version,
        type=this.type,
        key_property = this.key_property,
        resource_state = this.resource_state,
        key_value_string = this.key_value_string,
        object_type = this.object_type,
        c_traceWorkSpacePattern = this.c_traceWorkSpacePattern

    )
}