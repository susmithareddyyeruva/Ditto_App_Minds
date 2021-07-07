package com.ditto.storage.data.model

data class PatternPiecesAPI(
    var id: Int = 0,
    var imageUrl: String = "",
    var imageName: String = "",
    var size: String = "",
    var view: String = "",

    var pieceNumber: String = "",
    var description: String = "",
    var positionInTab: String = "",
    var tabCategory: String = "",
    var cutQuantity: String = "",

    var isSpliced: Boolean,
    var spliceDirection: String = "",

    var cutOnFold: String = "",
    var splicedImages: List<SpliceImagesAPI> = emptyList(),
)