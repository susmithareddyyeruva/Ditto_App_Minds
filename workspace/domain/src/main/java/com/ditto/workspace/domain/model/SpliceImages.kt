package com.ditto.workspace.domain.model

data class SpliceImages(
    var id: Int = 0,
    var row: Int = 0,
    var column: Int = 0,
    var reference_splice: String,
    var imagePath: String
)