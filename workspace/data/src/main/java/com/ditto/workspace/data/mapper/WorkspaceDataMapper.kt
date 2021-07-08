package com.ditto.workspace.data.mapper

import com.ditto.storage.data.model.WorkspaceData
import com.ditto.storage.data.model.WorkspaceItemAPI
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
    fun WorkspaceDataAPI.toDomain(): WorkspaceData {
        return WorkspaceData(
            tailornaovaDesignId = this.tailornaovaDesignId,
            selectedTab = this.selectedTab,
            status = this.status,
            numberOfPieces = this.numberOfPieces.toDomain(),
            garmetWorkspaceItems = this.garmetWorkspaceItems.map { it.toDomain()},
            liningWorkspaceItems = this.liningWorkspaceItems.map { it.toDomain() },
            patternPiecesFromApi = this.patternPiecesFromApi.map { it.toDomain() },
            interfaceWorkspaceItems = this.interfaceWorkspaceItems.map { it.toDomain() }
        )
    }

    fun WorkspaceData.toDomain(): WorkspaceDataAPI {
        return WorkspaceDataAPI(
            tailornaovaDesignId = this.tailornaovaDesignId,
            selectedTab = this.selectedTab,
            status = this.status,
            numberOfPieces = this.numberOfPieces.toDomain(),
            garmetWorkspaceItems = this.garmetWorkspaceItems.map { it.toDomain()},
            liningWorkspaceItems = this.liningWorkspaceItems.map { it.toDomain() },
            patternPiecesFromApi = this.patternPiecesFromApi.map { it.toDomain() },
            interfaceWorkspaceItems = this.interfaceWorkspaceItems.map { it.toDomain() }
        )


    }


    fun NumberOfPieces.toDomain(): com.ditto.storage.data.model.NumberOfPiecesStorage {
        return com.ditto.storage.data.model.NumberOfPiecesStorage(
            garment = this.garment,
            lining = this.lining,
            interfacee = this.interfacee
        )
    }

    fun com.ditto.storage.data.model.NumberOfPiecesStorage.toDomain(): NumberOfPieces {
        return NumberOfPieces(
            garment = this.garment,
            lining = this.lining,
            interfacee = this.interfacee
        )
    }


    fun PatternPieceDomain.toDomain(): com.ditto.storage.data.model.PatternPiecesFromApiWorkspcaeData {
        return com.ditto.storage.data.model.PatternPiecesFromApiWorkspcaeData(
            id = this.id,
            isCompleted = this.isCompleted
        )
    }

    fun com.ditto.storage.data.model.PatternPiecesFromApiWorkspcaeData.toDomain(): PatternPieceDomain {
        return PatternPieceDomain(
            id = this.id,
            isCompleted = this.isCompleted
        )
    }

    fun com.ditto.storage.data.model.WorkspaceItemAPI.toDomain():WorkspaceItemAPIDomain{
        return WorkspaceItemAPIDomain(
            id=this.id,
            patternPiecesId=this.patternPiecesId,
            isCompleted=this.isCompleted,
            xcoordinate=this.xcoordinate,
            ycoordinate=this.ycoordinate,
            pivotX=this.pivotX,
            pivotY=this.pivotY,
            transformA=this.transformA,
            transformD=this.transformD,
            rotationAngle=this.rotationAngle,
            isMirrorH=this.isMirrorH,
            isMirrorV=this.isMirrorV,
            showMirrorDialog=this.showMirrorDialog,
            currentSplicedPieceNo=this.currentSplicedPieceNo,
        )
    }

    fun WorkspaceItemAPIDomain.toDomain():com.ditto.storage.data.model.WorkspaceItemAPI{
        return com.ditto.storage.data.model.WorkspaceItemAPI(
            id=this.id,
            patternPiecesId=this.patternPiecesId,
            isCompleted=this.isCompleted,
            xcoordinate=this.xcoordinate,
            ycoordinate=this.ycoordinate,
            pivotX=this.pivotX,
            pivotY=this.pivotY,
            transformA=this.transformA,
            transformD=this.transformD,
            rotationAngle=this.rotationAngle,
            isMirrorH=this.isMirrorH,
            isMirrorV=this.isMirrorV,
            showMirrorDialog=this.showMirrorDialog,
            currentSplicedPieceNo=this.currentSplicedPieceNo,
        )
    }


/*fun CTraceWorkSpacePatternInputData.toDomain(): WorkspaceDataAPI {
    return WorkspaceDataAPI(
        tailornaovaDesignId = this.tailornaovaDesignId,
        selectedTab = this.selectedTab,
        status = this.status,
        numberOfPieces = this.numberOfCompletedPiece.toDomainn(),
        liningWorkspaceItems = this.liningWorkspaceItems.map { it.toDomainStorage() },
        garmetWorkspaceItems = this.garmetWorkspaceItems.map { it.toDomain() },
        patternPiecesFromApi = this.patternPieces.map { it.toDomain() },
        interfaceWorkspaceItems = this.interfaceWorkspaceItem.map { it.toDomain() }

    )
}*/

fun NumberOfPieces.toDomainn(): NumberOfPieces{
    return com.ditto.workspace.domain.model.NumberOfPieces(
        garment=this.garment,
        lining=this.lining,
        interfacee=this.interfacee
    )
}

fun WorkspaceItemAPIDomain.toDomainStorage():com.ditto.storage.data.model.WorkspaceItemAPI{
    return com.ditto.storage.data.model.WorkspaceItemAPI(
        id=this.id,
        patternPiecesId=this.patternPiecesId,
        isCompleted=this.isCompleted,
        xcoordinate=this.xcoordinate,
        ycoordinate=this.ycoordinate,
        pivotX=this.pivotX,
        pivotY=this.pivotY,
        transformA=this.transformA,
        transformD=this.transformD,
        rotationAngle=this.rotationAngle,
        isMirrorH=this.isMirrorH,
        isMirrorV=this.isMirrorV,
        showMirrorDialog=this.showMirrorDialog,
        currentSplicedPieceNo=this.currentSplicedPieceNo,
    )
}
   /* fun List<WorkspaceItemAPIWorkspace>.toDomain():List<WorkspaceItemAPI>{*/
   /*     return this.map{*/
   /*         WorkspaceItemAPI(*/
   /*             id = it.id,*/
   /*             patternPiecesId = it.patternPiecesId,*/
   /*             isCompleted = it.isCompleted,*/
   /*             xcoordinate = it.xcoordinate,*/
   /*             ycoordinate = it.ycoordinate,*/
   /*             pivotX = it.pivotX,*/
   /*             pivotY = it.pivotY,*/
   /*             transformA = it.transformA,*/
   /*             transformD = it.transformD,*/
   /*             rotationAngle = it.rotationAngle,*/
   /*             isMirrorH = it.isMirrorH,*/
   /*             isMirrorV = it.isMirrorV,*/
   /*             showMirrorDialog = it.showMirrorDialog,*/
   /*             currentSplicedPieceNo = it.currentSplicedPieceNo,*/
   /*         )*/
   /*     }*/
   /* }*/

    fun List<WorkspaceItemAPI>.toDomain():List<WorkspaceItemAPIWorkspace>{
        return this.map{
            WorkspaceItemAPIWorkspace(
                id = it.id,
                patternPiecesId = it.patternPiecesId,
                isCompleted = it.isCompleted,
                xcoordinate = it.xcoordinate,
                ycoordinate = it.ycoordinate,
                pivotX = it.pivotX,
                pivotY = it.pivotY,
                transformA = it.transformA,
                transformD = it.transformD,
                rotationAngle = it.rotationAngle,
                isMirrorH = it.isMirrorH,
                isMirrorV = it.isMirrorV,
                showMirrorDialog = it.showMirrorDialog,
                currentSplicedPieceNo = it.currentSplicedPieceNo,
            )
        }
    }
