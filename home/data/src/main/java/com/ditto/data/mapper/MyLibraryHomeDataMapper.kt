package com.ditto.data.mapper

import com.ditto.data.model.MyLibraryResult
import com.ditto.data.model.Prod
import com.ditto.home.domain.model.*
import com.ditto.home.domain.model.ProdDomain
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.model.PatternPieceDataDomain
import com.ditto.mylibrary.domain.model.SplicedImageDomain
import com.ditto.storage.data.model.*
import com.ditto.storage.data.model.PatternPieceData
import com.ditto.storage.data.model.SelvageData
import com.ditto.storage.data.model.SplicedImageData

fun MyLibraryResult.toDomain():MyLibraryDetailsDomain{
    return MyLibraryDetailsDomain(
      action = this.action?:"",
        locale = this.locale?:"",
        prod = this.prod?.map { it.toDomain() }?: emptyList(),
        queryString = this.queryString?:"",
        currentPageId = this.currentPageId,
        totalPageCount = this.totalPageCount?:0,
        totalPatternCount = this.totalPatternCount?:0
    )
}

fun Prod.toDomain(): ProdDomain {
    return ProdDomain(
        iD = this.iD?:"",
        prodBrand = this.prodBrand?:"",
        prodGender = this.prodGender?:"",
        name = this.prodName?:"",
        patternType = this.patternType?:"",
        prodSize = this.prodSize?:"",
        tailornovaDesignId = this.tailornovaDesignId?:"",
        creationDate = this.creationDate?:"",
        image = this.image?:"",
        customization = this.customization?:"",
        dateOfModification = this.dateOfModification?:"",
        occasion = this.occasion?:"",
        season = this.season?:"",
        status = this.status?:"",
        subscriptionExpiryDate = this.subscriptionExpiryDate?:"",
        suitableFor = this.suitableFor?:"",
        type = this.type?:""
    )
}


//OfflinePatterns(DB table)>>OfflinePatternData(model)


internal fun List<OfflinePatterns>.toDomain(): List<OfflinePatternData>
{
    return this.map {
        OfflinePatternData(
            tailornaovaDesignId = it.tailornaovaDesignId,
            selectedTab = it.selectedTab,
            status = it.status,
            numberOfCompletedPieces = it.numberOfCompletedPieces.toDomainOfflinePicecs(),
            patternPiecesFromApi = it.patternPiecesFromApi.map { it.toDomain1() },
            garmetWorkspaceItemOfflines = it.garmetWorkspaceItemOfflines?.map { it.toDomain1() }
                .toMutableList(),
            liningWorkspaceItemOfflines = it.liningWorkspaceItemOfflines?.map { it.toDomain1() }
                .toMutableList(),
            interfaceWorkspaceItemOfflines = it.interfaceWorkspaceItemOfflines?.map { it.toDomain1() }
                ?.toMutableList(),
            id = it.id,
            name = it.name,
            description = it.description,
            patternType = it.patternType,
            numberOfPieces = it.numberOfPieces.toDomainOfflinePicecs(),
            orderModificationDate = it.orderModificationDate,
            orderCreationDate = it.orderCreationDate,
            instructionFileName = it.instructionFileName,
            instructionUrl = it.instructionUrl,
            thumbnailImageUrl = it.thumbnailImageUrl,
            thumbnailImageName = it.thumbnailImageName,
            thumbnailEnlargedImageName = it.thumbnailEnlargedImageName,
            patternDescriptionImageUrl = it.patternDescriptionImageUrl,
            selvages = it.selvages.map { it.toDomainStorage() },
            patternPieces = it.patternPieces.map { it.toDomainn() },
            brand = it.brand,
            size = it.size,
            gender = it.gender,
            customization = it.customization,
            dressType = it.dressType,
            suitableFor = it.suitableFor,
            occasion = it.occasion
        )
    }
}

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

fun NumberOfCompletedPiecesOffline.toDomainOfflinePicecs(): OfflineNumberOfCompletedPiece {
    return OfflineNumberOfCompletedPiece(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`
    )
}

fun PatternPiecesOffline.toDomain1(): OfflinePatternPieces {
    return OfflinePatternPieces(
        id = this.id,
        isCompleted = this.isCompleted
    )
}

