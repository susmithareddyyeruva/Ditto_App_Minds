package com.ditto.workspace.domain.model

data class NumberOfCompletedPieceDomain(
    val garment: Int? = 0,
    val lining: Int? = 0,
    val `interface`: Int? = 0
)