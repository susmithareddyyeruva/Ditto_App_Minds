package com.ditto.workspace.data.mapper

import android.util.Log
import com.ditto.workspace.domain.model.*
import non_core.lib.Result


fun PatternPieces.toPatternPieceDomain(): PatternPieceSFCCAPI {
    return PatternPieceSFCCAPI(
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
        transformA = this.transformA,
        transformD = this.transformD,
        rotationAngle = this.rotationAngle,
        mirrorOption=this.mirrorOption,
        isMirrorH = this.isMirrorH,
        isMirrorV = this.isMirrorV,
        showMirrorDialog = this.showMirrorDialog,
        currentSplicedPieceColumn=this.currentSplicedPieceColumn,
        currentSplicedPieceRow=this.currentSplicedPieceRow,
        currentSplicedPieceNo = "currentSplicedPieceNo"
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
        mapImageUrl = this.mapImageUrl,
        mapImageName=this.mapImageName
    )
}

fun PatternPieceDataDomain.toOldModel(patternPieces: List<PatternPieceSFCCAPI>?): PatternPieces {

    val patternPiece = getIsComplete(this.id, patternPieces)

    return PatternPieces(
        id = this.id,
        parentPattern = "parentPattern",
        imagePath = this.imageUrl,
        imageName=this.imageName,
        thumbnailImageUrl = this.thumbnailImageUrl,
        thumbnailImageName=this.thumbnailImageName,
        size = this.size,
        view = this.view,
        pieceNumber = this.pieceNumber,
        pieceDescription = this.pieceDescription,
        positionInTab = this.positionInTab,
        tabCategory = this.tabCategory,
        cutQuantity = this.cutQuantity,
        splice = this.isSpliced,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map {
            it.toOldModel()
        },
        cutOnFold = this.cutOnFold.toString(),
        isMirrorOption = this.isMirrorOption,
        isCompleted = patternPiece?.isCompleted ?: false,
        contrast = this.contrast
    )
}

fun SplicedImageDomain.toOldModelSpliceImage(): SpliceImages {
    return SpliceImages(
        id = this.id,
        row = this.row,
        column = this.column,
        reference_splice = this.mapImageName,
        imageName = this.imageName,
        imagePath = this.imageUrl,
        mapImageUrl=this.mapImageUrl,
        mapImageName=this.mapImageName
    )
}



fun WorkspaceItemDomain.toOldModel(patternPieces: List<PatternPieceDataDomain>?): WorkspaceItems {

    val patternPiece = getPatternPiece(this.patternPiecesId, patternPieces)

    return WorkspaceItems(
        id = this.id,
        parentPattern = "parentPattern",
        imagePath = patternPiece?.imageUrl,
        imageName = patternPiece?.imageName,
        size = patternPiece?.size,
        view = patternPiece?.view,
        pieceNumber = patternPiece?.pieceNumber,
        pieceDescription = patternPiece?.pieceDescription,
        positionInTab = patternPiece?.positionInTab,
        tabCategory = patternPiece?.tabCategory,
        cutQuantity = patternPiece?.cutQuantity ?: "",
        splice = patternPiece?.isSpliced ?: false,
        //spliceDirection = patternPiece?.spliceDirection,
        spliceScreenQuantity = patternPiece?.spliceScreenQuantity,
        cutOnFold = patternPiece?.cutOnFold.toString(),
        mirrorOption = patternPiece?.isMirrorOption,
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
        currentSplicedPieceColumn = this.currentSplicedPieceColumn,
        transformA = this.transformA,
        transformD = this.transformD
    )
}

private fun getPatternPiece(
    patternPiecesId: Int?,
    patternPieces: List<PatternPieceDataDomain>?
): PatternPieceDataDomain? {
    return patternPieces?.find { it.id == patternPiecesId }
}

private fun getIsComplete(
    patternPiecesId: Int?,
    patternPieces: List<PatternPieceSFCCAPI>?
): PatternPieceSFCCAPI? {
    return patternPieces?.find { it.id == patternPiecesId }
}


fun WorkspaceItemOfflineDomain.toOldModelOffline(patternPieces: List<PatternPieceDataDomain>?):WorkspaceItems{
    val patternPiece= getPatternPiece(this.patternPiecesId,patternPieces)
    return WorkspaceItems(
        id = this.id,
        parentPattern = "parentPattern",
        imagePath = patternPiece?.imageUrl,
        imageName = patternPiece?.imageName,
        size = patternPiece?.size,
        view = patternPiece?.view,
        pieceNumber = patternPiece?.pieceNumber,
        pieceDescription = patternPiece?.pieceDescription,
        positionInTab = patternPiece?.positionInTab,
        tabCategory = patternPiece?.tabCategory,
        cutQuantity = patternPiece?.cutQuantity ?: "",
        splice = patternPiece?.isSpliced ?: false,
        //spliceDirection = patternPiece?.spliceDirection,
        spliceScreenQuantity = patternPiece?.spliceScreenQuantity,
        cutOnFold = patternPiece?.cutOnFold.toString(),
        mirrorOption = patternPiece?.isMirrorOption,
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
        currentSplicedPieceColumn = this.currentSplicedPieceColumn,
        transformD = this.transformD,
        transformA = this.transformA
    )
}

fun PatternPieceDataDomain.toOldModelOffline(patternPieces: List<PatternPieceSFCCAPI>): PatternPieces {
    val patternPiece: PatternPieceSFCCAPI? = getIsComplete(this.id, patternPieces)
    return PatternPieces(
        id = this.id,
        cutQuantity = this.cutQuantity,
        imageName = this.imageName,
        imagePath = this.imageUrl,
        isCompleted = patternPiece?.isCompleted ?: false,
        isMirrorOption = this.isMirrorOption,
        parentPattern = "parentPattern",
        splice = this.isSpliced,
        tabCategory = this.tabCategory,
        size = this.size,
        pieceDescription = this.pieceDescription,
        cutOnFold = this.cutOnFold.toString(),
        positionInTab = this.positionInTab,
        pieceNumber = this.pieceNumber,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailImageUrl = this.thumbnailImageUrl,
        view = this.view,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map {
            it.toOldOffline()
        },
        contrast = this.contrast
    )
}

fun SplicedImageDomain.toOldOffline(): SpliceImages {

    return SpliceImages(
        id = this.id,
        row = this.row,
        column = this.column,
        reference_splice = "reference_splice",
        imageName = this.imageName,
        imagePath = this.imageUrl,
        mapImageUrl = this.mapImageUrl,
        mapImageName = this.mapImageName,
        )
}

// mapping WorkspaceAPI response model to PatternData model
fun combineTailornovaAndSFCCDetails(
    resultTailernova: Result.OnSuccess<OfflinePatternData>,
    fetchWorkspaceResult: Result.OnSuccess<WorkspaceDataAPI>
): PatternsData {

    return PatternsData(
        id = resultTailernova.data.id,
        patternName = resultTailernova.data.patternName,
        patternType = resultTailernova.data.patternType,
        description = resultTailernova.data.description,
        totalPieces = 0,
        completedPieces = 0,
        numberOfCompletedPiece = if(fetchWorkspaceResult.data.numberOfCompletedPiece==null)
            NumberOfPieces(
                garment = 0,
                lining = 0,
                `interface` = 0,
                other = 0
            ) else fetchWorkspaceResult.data.numberOfCompletedPiece,
        totalNumberOfPieces = resultTailernova.data.numberOfPieces,
        selectedTab = fetchWorkspaceResult.data.selectedTab,
        status = fetchWorkspaceResult.data.status,
        thumbnailImagePath = resultTailernova.data.thumbnailImageUrl,
        thumbnailImageName = resultTailernova.data.thumbnailImageName,
        instructionUrl = resultTailernova.data.instructionUrl,
        //descriptionImages //will come from tailernova in next sprints
        selvages = resultTailernova.data.selvages?.map { it.toOldModel() },
        patternPieces = resultTailernova.data.patternPiecesTailornova?.map { it.toOldModel(fetchWorkspaceResult.data.patternPieces) },
        garmetWorkspaceItemOfflines = fetchWorkspaceResult.data.garmetWorkspaceItems?.map {
            it.toOldModel(
                resultTailernova.data.patternPiecesTailornova
            )
        }?.toMutableList(),

        liningWorkspaceItemOfflines = fetchWorkspaceResult.data.liningWorkspaceItems?.map {
            it.toOldModel(
                resultTailernova.data.patternPiecesTailornova
            )
        }?.toMutableList(),
        interfaceWorkspaceItemOfflines = fetchWorkspaceResult.data.interfaceWorkspaceItems?.map {
            it.toOldModel(
                resultTailernova.data.patternPiecesTailornova
            )
        }?.toMutableList(),
        otherWorkspaceItemOfflines = fetchWorkspaceResult.data.otherWorkspaceItems?.map {
            it.toOldModel(
                resultTailernova.data.patternPiecesTailornova
            )
        }?.toMutableList()
    )
}

// mapping WorkspaceAPI response model to PatternData model for offline
fun combineTailornovaAndSFCCDetails(
    resultTailernova: Result.OnSuccess<OfflinePatternData>
): PatternsData {

    return PatternsData(
        id = resultTailernova.data.id,
        patternName = resultTailernova.data.patternName,
        patternType = resultTailernova.data.patternType,
        description = resultTailernova.data.description,
        totalPieces = 0,
        completedPieces = 0,
        numberOfCompletedPiece = if(resultTailernova.data.numberOfCompletedPieces ==null)
            NumberOfPieces(
                garment = 0,
                lining = 0,
                `interface` = 0,
                other = 0
            ) else resultTailernova.data.numberOfCompletedPieces,
        totalNumberOfPieces = resultTailernova.data.numberOfPieces,
        selectedTab = resultTailernova.data.selectedTab,
        status = resultTailernova.data.status,
        thumbnailImagePath = resultTailernova.data.thumbnailImageUrl,
        thumbnailImageName = resultTailernova.data.thumbnailImageName,
        instructionUrl = resultTailernova.data.instructionUrl,
        //descriptionImages // will come from tailernova in next sprints
        selvages = resultTailernova.data.selvages?.map { it.toOldModel() },
        patternPieces = resultTailernova.data.patternPiecesTailornova?.map {
            it.toOldModelOffline(
                resultTailernova
                    .data
                    .patternPiecesFromApi
            )
        },
        garmetWorkspaceItemOfflines = resultTailernova.data.garmetWorkspaceItemOfflines.map {
            it.toOldModelOffline(resultTailernova.data.patternPiecesTailornova)
        }.toMutableList(),

        liningWorkspaceItemOfflines = resultTailernova.data.liningWorkspaceItemOfflines.map {
            it.toOldModelOffline(
                resultTailernova.data.patternPiecesTailornova
            )
        }.toMutableList(),
        interfaceWorkspaceItemOfflines = resultTailernova.data.interfaceWorkspaceItemOfflines.map {
            it.toOldModelOffline(
                resultTailernova.data.patternPiecesTailornova
            )
        }.toMutableList(),
        otherWorkspaceItemOfflines = resultTailernova.data.otherWorkspaceItemOfflines.map {
            it.toOldModelOffline(
                resultTailernova.data.patternPiecesTailornova
            )
        }.toMutableList()
    )
}

fun getWorkspaceInputDataToAPI(patternData: PatternsData?): WorkspaceDataAPI {

    Log.d("getWSInputDataToAPI", "OnSuccess patternData: $patternData")

    val cTraceWorkSpacePatternInputData = WorkspaceDataAPI(
        tailornaovaDesignId = patternData?.id,
        selectedTab = patternData?.selectedTab,
        status = patternData?.status,
        numberOfCompletedPiece = patternData?.numberOfCompletedPiece,
        patternPieces = patternData?.patternPieces?.map { it.toPatternPieceDomain() },

        garmetWorkspaceItems = patternData?.garmetWorkspaceItemOfflines?.map {
            it.toWorkspaceItemDomain()
        }?.toMutableList(),
        liningWorkspaceItems = patternData?.liningWorkspaceItemOfflines?.map { it.toWorkspaceItemDomain() }
            ?.toMutableList(),
        interfaceWorkspaceItems = patternData?.interfaceWorkspaceItemOfflines?.map { it.toWorkspaceItemDomain()
        }?.toMutableList(),
        otherWorkspaceItems = patternData?.otherWorkspaceItemOfflines?.map { it.toWorkspaceItemDomain()
        }?.toMutableList()
    )
    Log.d(
        "getWSInputDataToAPI",
        "OnSuccess cTraceWSPatternInputData: $cTraceWorkSpacePatternInputData"
    )

    return cTraceWorkSpacePatternInputData
}
