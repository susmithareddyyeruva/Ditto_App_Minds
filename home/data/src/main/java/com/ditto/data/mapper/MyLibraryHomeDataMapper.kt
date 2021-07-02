package com.ditto.data.mapper

import com.ditto.data.model.MyLibraryResult
import com.ditto.data.model.Prod
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.model.ProdDomain

fun MyLibraryResult.toDomain():MyLibraryDetailsDomain{
    return MyLibraryDetailsDomain(
      action = this.action,
        locale = this.locale,
        prod = this.prod.map { it.toDomain() },
        queryString = this.queryString
    )
}

fun Prod.toDomain(): ProdDomain {
    return ProdDomain(
        ID = this.iD,
        prodBrand = this.prodBrand,
        prodGender = this.prodGender,
        prodName = this.prodName,
        patternType = this.patternType,
        prodSize = this.prodSize,
        tailornovaDesignId = this.tailornovaDesignId,
        tailornovaDesignName = this.tailornovaDesignName

    )
}