package com.ditto.storage.data.model

data class SplicedImageData(
    val column: Int,
    val designId: String?="",
    val id: Int,
    val imageName: String?="",
    val imageUrl: String?="",
    val mapImageName: String?="",
    val pieceId: Int,
    val row: Int
)