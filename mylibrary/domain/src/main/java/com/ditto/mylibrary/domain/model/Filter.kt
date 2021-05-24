package com.ditto.mylibrary.domain.model

object Filter {
    var genderList = arrayListOf<FilterItems>()
    var categoryList = arrayListOf<FilterItems>()
    var brandList = arrayListOf<FilterItems>()
    var sizeList = arrayListOf<FilterItems>()
    var typeList = arrayListOf<FilterItems>()
    var seasonList = arrayListOf<FilterItems>()
    var occasionList = arrayListOf<FilterItems>()
    var suitableList = arrayListOf<FilterItems>()
    var customizationList = arrayListOf<FilterItems>()
    fun clearAll() {
        genderList.clear()
        categoryList.clear()
        brandList.clear()
        sizeList.clear()
        typeList.clear()
        seasonList.clear()
        occasionList.clear()
        suitableList.clear()
        customizationList.clear()
    }
}