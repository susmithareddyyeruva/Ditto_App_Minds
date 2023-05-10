package com.ditto.mylibrary.domain.model

data class ThirdPartyDomain(
    val variationDomain: List<VariationDomain>? = emptyList(),
    val name: String?,
    val brand: String?,
    val description: String?,
    val image: String?,
    val notionsDetails: String?,
    val yardageDetails: List<String>? = emptyList(),
    val yardageImageUrl: String?,
    val yardagePdfUrl: String?,
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
