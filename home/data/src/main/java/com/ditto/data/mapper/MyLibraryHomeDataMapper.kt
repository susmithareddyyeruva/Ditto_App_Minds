package com.ditto.data.mapper

import com.ditto.data.model.MyLibraryResult
import com.ditto.data.model.Prod
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.model.ProdDomain

fun MyLibraryResult.toDomain():MyLibraryDetailsDomain{
    return MyLibraryDetailsDomain(
      action = this.action?:"",
        locale = this.locale?:"",
        prod = this.prod?.map { it.toDomain() }?: emptyList(),
        queryString = this.queryString?:"",
        currentPageId = this.currentPageId,
        totalPageCount = this.totalPageCount?:0,
        totalPatternCount = this.totalPatternCount?:0
    )
}

fun Prod.toDomain(): ProdDomain {
    return ProdDomain(
        iD = this.iD,
        prodBrand = this.prodBrand?:"",
        prodGender = this.prodGender?:"",
        name = this.prodName?:"",
        patternType = this.patternType?:"",
        prodSize = this.prodSize?:"",
        tailornovaDesignId = this.tailornovaDesignId?:"",
        creationDate = this.creationDate?:"",
        image = this.image?:"",
        customization = this.customization?:"",
        dateOfModification = this.dateOfModification?:"",
        occasion = this.occasion?:"",
        season = this.season?:"",
        status = this.status?:"",
        subscriptionExpiryDate = this.subscriptionExpiryDate?:"",
        suitableFor = this.suitableFor?:"",
        type = this.type?:""


    )
}
