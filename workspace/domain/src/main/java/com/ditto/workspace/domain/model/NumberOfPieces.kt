package com.ditto.workspace.domain.model

data class NumberOfPieces(
    var garment: Int? = 0,
    var lining: Int? = 0,
    var `interface`: Int? = 0,
    val other: Int? = 0
)