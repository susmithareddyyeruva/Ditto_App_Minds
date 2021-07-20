package com.ditto.data.mapper

import com.ditto.data.model.HomeFilterMenu
import com.ditto.data.model.MyLibraryResult
import com.ditto.data.model.Prod
import com.ditto.home.domain.model.HomeFilterMenuDomain
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.model.ProdDomain

fun MyLibraryResult.toDomain():MyLibraryDetailsDomain{
    return MyLibraryDetailsDomain(
      action = this.action,
        locale = this.locale,
        prod = this.prod.map { it.toDomain() },
        queryString = this.queryString,
        currentPageId = this.currentPageId,
        totalPageCount = this.totalPageCount,
        totalPatternCount = this.totalPatternCount,
        filter = this.filter.toDomain()
    )
}
fun HomeFilterMenu.toDomain():HomeFilterMenuDomain{
    return HomeFilterMenuDomain(
        category=this.category,
        size = this.size,
        gender = this.gender,
        brand = this.brand
    )
}
fun Prod.toDomain(): ProdDomain {
    return ProdDomain(
        iD = this.iD,
        prodBrand = this.prodBrand,
        prodGender = this.prodGender,
        prodName = this.prodName,
        patternType = this.patternType,
        prodSize = this.prodSize,
        tailornovaDesignId = this.tailornovaDesignId,
        creationDate = this.creationDate,
        image = this.image,
        customization = this.customization,
        dateOfModification = this.dateOfModification,
        longDescription = this.longDescription,
        occasion = this.occasion,
        season = this.season,
        status = this.status,
        subscriptionExpiryDate = this.subscriptionExpiryDate,
        suitableFor = this.suitableFor,
        type = this.type


    )
}
