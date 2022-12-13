package com.ditto.mylibrary.domain.model

data class ThirdPartyDomain(
    val variationDomain: List<VariationDomain>? = emptyList()
)
data class VariationDomain(
    val sizeDomain: List<SizeDomain>? = emptyList(),
    val style: String?,
    val styleName: String?,
)

data class SizeDomain(
    val designID: String?,
    val mannequinID: String?,
    val size: String?,
)
