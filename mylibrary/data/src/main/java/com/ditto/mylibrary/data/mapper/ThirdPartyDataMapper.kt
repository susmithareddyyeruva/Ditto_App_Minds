package com.ditto.mylibrary.data.mapper

import com.ditto.mylibrary.domain.model.SizeDomain
import com.ditto.mylibrary.domain.model.ThirdPartyDomain
import com.ditto.mylibrary.domain.model.VariationDomain
import com.ditto.mylibrary.model.Size
import com.ditto.mylibrary.model.ThirdPartyResponse
import com.ditto.mylibrary.model.Variation

fun ThirdPartyResponse.toDomain() : ThirdPartyDomain {
    return ThirdPartyDomain(
        variationDomain = this.product?.brandVariantData?.variation?.map {
            it.toDomain()
        },
        name = this.product?.brandVariantData?.name ,
        brand = this.product?.brandVariantData?.brand,
        description = this.product?.brandVariantData?.description ,
        image = this.product?.brandVariantData?.image ,
        notionsDetails = this.product?.brandVariantData?.notionsDetails,
        yardageDetails = this.product?.brandVariantData?.yardageDetails,
        yardageImageUrl = this.product?.brandVariantData?.yardageImageUrl,
        yardagePdfUrl = this.product?.brandVariantData?.yardagePdfUrl,
        tailornovaDesignName = this.product?.brandVariantData?.tailornovaDesignName,
        customSizeFitName = this.product?.brandVariantData?.customSizeFitName,
        lastModifiedSizeDate = this.product?.brandVariantData?.lastModifiedSizeDate
    )
}

fun Variation.toDomain(): VariationDomain {
    return VariationDomain(
        sizeDomain = this.size?.map {
            it.toDomain()
        },
        style = this.style,
        styleName = this.styleName
    )
}

fun Size.toDomain(): SizeDomain {
    return SizeDomain(
        designID = this.designID,
        mannequinID = this.mannequinID,
        size = this.size
    )
}