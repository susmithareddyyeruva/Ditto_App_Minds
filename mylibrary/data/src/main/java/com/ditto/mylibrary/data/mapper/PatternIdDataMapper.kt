package com.ditto.mylibrary.data.mapper

import com.ditto.mylibrary.domain.model.NumberOfPiecesData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.SplicedImageData
import com.ditto.storage.data.model.OfflinePatternData
import com.ditto.storage.data.model.PatternPieceData
import com.ditto.storage.data.model.SelvageData

/*
internal fun PatternIdResponse.toDomain() : PatternIdData{
    return PatternIdData (
        designId = this.designId,
        name = this.name,
        description = this.description
    )
}*/

//PatternIdData>>OfflinePatternData

internal fun PatternIdData.toDomain(): OfflinePatternData {
    return OfflinePatternData(
        id = this.designId,
        tailornaovaDesignId = this.designId,
        name = this.name,
        description = this.description,
        patternType=this.patternType,
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
        description = this.description,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
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
        description = this.description,
        id = this.id,
        imageName = this.imageName,
        imageUrl = this.imageUrl,
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

