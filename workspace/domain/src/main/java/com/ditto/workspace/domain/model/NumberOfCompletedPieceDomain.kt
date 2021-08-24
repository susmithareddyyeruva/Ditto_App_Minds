package com.ditto.workspace.domain.model

data class NumberOfCompletedPieceDomain(
    var garment: Int? = 0,
    var lining: Int? = 0,
    var `interface`: Int? = 0
)