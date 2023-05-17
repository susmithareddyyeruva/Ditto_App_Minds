package com.ditto.mylibrary.data.mapper

import com.ditto.mylibrary.domain.model.*
import com.ditto.storage.data.model.*
import com.ditto.storage.data.model.MannequinData
import com.ditto.storage.data.model.PatternPieceData
import com.ditto.storage.data.model.SelvageData
import com.ditto.storage.data.model.SplicedImageData
import core.appstate.AppState

internal fun List<OfflinePatterns>.toDomain(): List<ProdDomain> {
    return this.map {
        ProdDomain(
            iD = it.productId,
            image = it.thumbnailImageName,
            prodName = it.patternName,
            description = it.description,
            creationDate = it.orderCreationDate,
            patternType = it.patternType,
            status = it.status,
            subscriptionExpiryDate = "",
            customization = "",
            dateOfModification = it.orderModificationDate,
            //type = it.dressType,
          //  season = "",
            occasion = "",
            suitableFor = "",
            tailornovaDesignId = it.tailornaovaDesignId,
            tailornovaDesignName = it.tailornovaDesignName,
            orderNo = it.orderNumber,
            prodSize = it.prodSize,
            prodGender = it.gender,
            prodBrand = it.brand,
            isFavourite = false,
            selectedMannequinId = it.selectedMannequinId,
            selectedMannequinName = it.selectedMannequinName,
            mannequin = it.mannequin?.map { it.toDomain() },
            yardageDetails = it.yardageDetails?.yardageDetails,
            notionDetails = it.yardageDetails?.notionDetails,
            yardagePdfUrl = it.yardagePdfUrl
        )
    }
}

internal fun OfflinePatterns.toDomain(): ProdDomain {
    return ProdDomain(
        iD = this.productId,
        image = this.thumbnailImageUrl,
        prodName = this.patternName,
        description = this.description,
        creationDate = this.orderCreationDate,
        patternType = this.patternType,
        status = this.status,
        subscriptionExpiryDate = "",
        customization = "",
        dateOfModification = this.orderModificationDate,
        //type = this.dressType,
       // season = "",
        occasion = "",
        suitableFor = "",
        tailornovaDesignId = this.tailornaovaDesignId,
        orderNo = this.orderNumber,
        prodSize = "",
        prodGender = "",
        prodBrand = "",
        isFavourite = false
    )
}

public fun PatternIdData.toDomain(
    orderNumber: String?,
    tailornovaDesignName: String?,
    prodSize: String?,
    status: String?,
    selectedMnnequinId: String?,
    selectedMannequinName: String?,
    mannequin: List<MannequinDataDomain>?,
    patternType:String?,
    lastDateOfModification: String?,
    selectedViewCupStyle: String?,
    yardagePdfUrl: String?,
    productId: String?
): OfflinePatterns {
    val mYardageDetails = YardageDetails(this.yardageDetails, this.notionDetails)
    val mHeroImageUrls = HeroImageUrls(this.heroImageUrls)
    return OfflinePatterns(
        custId = AppState.getCustID(),
        designId = this.designId,
        tailornaovaDesignId = this.designId,
        patternName = this.patternName,
        description = this.description,
        patternType = patternType,
        numberOfCompletedPieces = null,
        numberOfPieces = this.numberOfPieces?.toDomain(),
        orderModificationDate = this.orderModificationDate,
        orderCreationDate = this.orderCreationDate,
        instructionFileName = this.instructionFileName,
        instructionUrl = this.instructionUrl,
        thumbnailImageUrl = this.thumbnailImageUrl,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailEnlargedImageName = this.thumbnailEnlargedImageName,
        patternDescriptionImageUrl = this.patternDescriptionImageUrl,
        selvages = this.selvages?.map { it.toDomain() },
        patternPiecesFromTailornova = this.patternPieces?.map { it.toDomain() },
        brand = this.brand,
        size = this.size,
        gender = this.gender,
        customization = this.customization,
        dressType = this.dressType,
        suitableFor = this.suitableFor,
        occasion = this.occasion,
        orderNumber = orderNumber,
        tailornovaDesignName = tailornovaDesignName,
        prodSize = prodSize,
        status = status,
        selectedMannequinId = selectedMnnequinId,
        selectedMannequinName = selectedMannequinName,
        mannequin = mannequin?.map { it.toDomain() },
        yardageDetails = mYardageDetails,
        lastDateOfModification = lastDateOfModification,
        selectedViewCupStyle = selectedViewCupStyle,
        yardageImageUrl = this.yardageImageUrl,
        yardagePdfUrl = yardagePdfUrl,
        sizeChartUrl = this.sizeChartUrl,
        mainheroImageUrl = this.mainheroImageUrl,
        heroImageUrls = mHeroImageUrls,
        productId = productId
    )
}
fun MannequinDataDomain.toDomain(): MannequinData {
    return MannequinData(
        mannequinId = this.mannequinId?:"",
        mannequinName = this.mannequinName?:""
    )
}

fun com.ditto.mylibrary.domain.model.MannequinData.toDomain12(): MannequinDataDomain {
    return MannequinDataDomain(
        mannequinId = this.mannequinId?:"",
        mannequinName = this.mannequinName?:""
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
        mirrorOption = this.isMirrorOption,
        pieceNumber = this.pieceNumber,
        positionInTab = this.positionInTab,
        size = this.size,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        tabCategory = this.tabCategory,
        view = this.view,
        contrast = this.contrast
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
        isMirrorOption = this.mirrorOption,
        pieceNumber = this.pieceNumber,
        positionInTab = this.positionInTab,
        size = this.size,
        //spliceDirection = this.spliceDirection,
        spliceScreenQuantity = this.spliceScreenQuantity,
        splicedImages = this.splicedImages?.map { it.toDomain() },
        tabCategory = this.tabCategory,
        view = this.view,
        contrast = this.contrast
    )

}

fun com.ditto.storage.data.model.SplicedImageData.toDomain(): com.ditto.mylibrary.domain.model.SplicedImageData {
    return com.ditto.mylibrary.domain.model.SplicedImageData(
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

fun com.ditto.mylibrary.domain.model.SplicedImageData.toDomain(): com.ditto.storage.data.model.SplicedImageData {
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
fun MannequinData.toDomains(): com.ditto.mylibrary.domain.model.MannequinData {
    return com.ditto.mylibrary.domain.model.MannequinData(
        mannequinId=this.mannequinId,
        mannequinName=this.mannequinName
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
        `interface` = this.`interface`,
        other = this.other
    )
}


fun com.ditto.storage.data.model.NumberOfCompletedPiecesOffline.toDomain(): NumberOfPiecesData {
    return NumberOfPiecesData(
        garment = this.garment,
        lining = this.lining,
        `interface` = this.`interface`,
        other = this.other
    )
}


internal fun OfflinePatterns.toPatternIDDomain(): PatternIdData {
    return PatternIdData(
        brand = this.brand,
        customization = this.customization,
        description = this.description,
        patternType = this.patternType,
        designId = this.tailornaovaDesignId,
        tailornovaDesignName = this.tailornovaDesignName,
        dressType = this.dressType,
        gender = this.gender,
        instructionFileName = this.instructionFileName,
        instructionUrl = this.instructionUrl,
        patternName = this.patternName,
        numberOfPieces = this.numberOfPieces?.toDomain(),
        occasion = this.occasion,
        orderCreationDate = this.orderCreationDate,
        orderModificationDate = this.orderModificationDate,
        patternDescriptionImageUrl = this.patternDescriptionImageUrl,
        patternPieces = this.patternPiecesFromTailornova?.map { it.toDomain() },
        season = "",
        selvages = this.selvages?.map { it.toDomain() },
        size = this.size,
        suitableFor = this.suitableFor,
        thumbnailEnlargedImageName = this.thumbnailEnlargedImageName,
        thumbnailImageName = this.thumbnailImageName,
        thumbnailImageUrl = this.thumbnailImageUrl,
        selectedMannequinId = this.selectedMannequinId,
        selectedMannequinName = this.selectedMannequinName,
        mannequin = this.mannequin?.map { it.toDomains() },
        lastDateOfModification = this.lastDateOfModification,
        selectedViewCupStyle = this.selectedViewCupStyle,
        yardageImageUrl = this.yardageImageUrl,
        yardagePdfUrl = this.yardagePdfUrl,
        mainheroImageUrl = this.mainheroImageUrl,
        sizeChartUrl = this.sizeChartUrl,
        heroImageUrls = this.heroImageUrls?.heroImageUrls ?: emptyList()
    )
}


