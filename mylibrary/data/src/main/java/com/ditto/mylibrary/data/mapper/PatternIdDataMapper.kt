package com.ditto.mylibrary.data.mapper

import com.ditto.mylibrary.domain.model.NumberOfPiecesData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.domain.model.SplicedImageData
import com.ditto.storage.data.model.OfflinePatterns
import com.ditto.storage.data.model.PatternPieceData
import com.ditto.storage.data.model.SelvageData

internal fun List<OfflinePatterns>.toDomain(): List<ProdDomain> {
    return this.map {
        ProdDomain(
            iD = it.id,
            image = it.thumbnailImageName,
            prodName = it.name,
            description = it.description,
            creationDate = it.orderCreationDate,
            patternType = it.patternType,
            status = it.status,
            subscriptionExpiryDate = "",
            customization = "",
            dateOfModification = it.orderModificationDate,
            type = it.dressType,
            season = "",
            occasion = "",
            suitableFor = "",
            tailornovaDesignId = it.tailornaovaDesignId,
            orderNo = "",
            prodSize = "",
            prodGender = "",
            prodBrand = "",
            isFavourite = false
        )
    }
}


internal fun OfflinePatterns.toDomain(): ProdDomain {
    return ProdDomain(
        iD = this.id,
        image = this.thumbnailImageUrl,
        prodName = this.name,
        description = this.description,
        creationDate = this.orderCreationDate,
        patternType = this.patternType,
        status = this.status,
        subscriptionExpiryDate = "",
        customization = "",
        dateOfModification = this.orderModificationDate,
        type = this.dressType,
        season = "",
        occasion = "",
        suitableFor = "",
        tailornovaDesignId = this.tailornaovaDesignId,
        orderNo = "",
        prodSize = "",
        prodGender = "",
        prodBrand = "",
        isFavourite = false
    )
}

internal fun PatternIdData.toDomain(): OfflinePatterns {
    return OfflinePatterns(
        id = this.designId,
        tailornaovaDesignId = this.designId,
        name = this.name,
        description = this.description,
        patternType = this.patternType,
        numberOfCompletedPieces = this.numberOfPieces.toDomain(),
        numberOfPieces = this.numberOfPieces.toDomain(),
        orderModificationDate = this.orderModificationDate,
        orderCreationDate = this.orderCreationDate,
        instructionFileName = this.instructionFileName,
        instructionUrl = this.instructionUrl,
        thumbnailImageUrl = this.thumbnailImageUrl,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailEnlargedImageName = this.thumbnailEnlargedImageName,
        patternDescriptionImageUrl = this.patternDescriptionImageUrl,
        selvages = this.selvages.map { it.toDomain() },
        patternPieces = this.patternPieces.map { it.toDomain() },
        brand = this.brand,
        size = this.size,
        gender = this.gender,
        customization = this.customization,
        dressType = this.dressType,
        suitableFor = this.suitableFor,
        occasion = this.occasion
    )
}

fun PatternPieceData.toDomain(): com.ditto.mylibrary.domain.model.PatternPieceData {
    return com.ditto.mylibrary.domain.model.PatternPieceData(
        cutOnFold = this.cutOnFold,
        cutQuantity = this.cutQuantity,
        pieceDescription = this.pieceDescription,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailImageUrl = this.thumbnailImageUrl,
        isSpliced = this.isSpliced,
        pieceNumber = this.pieceNumber,
        positionInTab = this.positionInTab,
        size = this.size,
        spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages.map { it.toDomain() },
        tabCategory = this.tabCategory,
        view = this.view
    )
}

fun com.ditto.mylibrary.domain.model.PatternPieceData.toDomain(): PatternPieceData {

    return PatternPieceData(
        cutOnFold = this.cutOnFold,
        cutQuantity = this.cutQuantity,
        pieceDescription = this.pieceDescription,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        thumbnailImageUrl = this.thumbnailImageUrl,
        thumbnailImageName = this.thumbnailImageName,
        isSpliced = this.isSpliced,
        pieceNumber = this.pieceNumber,
        positionInTab = this.positionInTab,
        size = this.size,
        spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages.map { it.toDomain() },
        tabCategory = this.tabCategory,
        view = this.view
    )

}
/*PatternPieceData>>PatternPieceData
s>>m*/

fun com.ditto.storage.data.model.SplicedImageData.toDomain(): SplicedImageData {
    return SplicedImageData(
        column = this.column,
        designId = this.designId,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        mapImageName = this.mapImageName,
        mapImageUrl = this.mapImageUrl,
        pieceId = this.pieceId,
        row = this.row
    )
}

fun SplicedImageData.toDomain(): com.ditto.storage.data.model.SplicedImageData {
    return com.ditto.storage.data.model.SplicedImageData(
        column = this.column,
        designId = this.designId,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        mapImageName = this.mapImageName,
        mapImageUrl = this.mapImageUrl,
        pieceId = this.pieceId,
        row = this.row
    )
}

fun SelvageData.toDomain(): com.ditto.mylibrary.domain.model.SelvageData {
    return com.ditto.mylibrary.domain.model.SelvageData(
        fabricLength = this.fabricLength,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        tabCategory = this.tabCategory
    )
}

fun com.ditto.mylibrary.domain.model.SelvageData.toDomain(): SelvageData {
    return SelvageData(
        fabricLength = this.fabricLength,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
        tabCategory = this.tabCategory
    )
}

fun NumberOfPiecesData.toDomain(): com.ditto.storage.data.model.NumberOfCompletedPiecesOffline {
    return com.ditto.storage.data.model.NumberOfCompletedPiecesOffline(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`
    )
}

fun com.ditto.storage.data.model.NumberOfCompletedPiecesOffline.toDomain(): NumberOfPiecesData {
    return NumberOfPiecesData(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`
    )
}


internal fun OfflinePatterns.toPatternIDDomain(): PatternIdData {
    return PatternIdData(
        brand = this.brand,
        customization = this.customization,
        description = this.description,
        patternType = this.patternType,
        designId = this.tailornaovaDesignId,
        dressType = this.dressType,
        gender = this.gender,
        instructionFileName = this.instructionFileName,
        instructionUrl = this.instructionUrl,
        name = this.name,
        numberOfPieces = this.numberOfPieces.toDomain(),
        occasion = this.occasion,
        orderCreationDate = this.orderCreationDate,
        orderModificationDate = this.orderModificationDate,
        patternDescriptionImageUrl = this.patternDescriptionImageUrl,
        patternPieces = emptyList(),
        season = "",
        selvages = emptyList(),
        size = this.size,
        suitableFor = this.suitableFor,
        thumbnailEnlargedImageName = this.thumbnailEnlargedImageName,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailImageUrl = this.thumbnailImageUrl
    )
}


