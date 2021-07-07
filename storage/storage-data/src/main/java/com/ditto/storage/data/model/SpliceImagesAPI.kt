package com.ditto.storage.data.model

data class SpliceImagesAPI (
    var designId:String = "",
    var pieceId: Int = 0,
    var id: Int = 0,
    var row: Int = 0,
    var column: Int = 0,
    var imageUrl: String = "",
    var imageName: String = "",
    var mapImageName: String = ""
)