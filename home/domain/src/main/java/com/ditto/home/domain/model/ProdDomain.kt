package com.ditto.home.domain.model

data class ProdDomain(
    var ID: String,
    var hasHighRiskIndicator: Boolean,
    var isDiscountApplied: Boolean,
    var isProjectableProduct: Boolean,
    var isTailornovaProduct: Boolean,
    var prodBrand: String,
    var prodGender: String,
    var prodName: String,
    var prodPrice: String,
    var tailornovaDesignId: String,
    var tailornovaDesignName: String
)