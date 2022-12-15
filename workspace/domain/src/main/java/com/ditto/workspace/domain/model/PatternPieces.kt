package com.ditto.workspace.domain.model

data class PatternPieces(
    var id: Int = 0,
    var parentPattern: String,
    var imagePath: String?,
    var imageName: String?,
    var thumbnailImageUrl: String?,
    var thumbnailImageName: String?,
    var size: String?,
    var view: String?,
    var pieceNumber: String?,
    var pieceDescription: String?,
    var positionInTab: String?,
    var tabCategory: String?,
    var cutQuantity: String?,
    var splice: Boolean? = false,
    //var spliceDirection: String? = "",
    var spliceScreenQuantity: String?,
    var splicedImages: List<SpliceImages>? = emptyList(),
    var cutOnFold: String?,
    var isMirrorOption: Boolean?,
    var isCompleted: Boolean = false,
    var contrast: String?
)