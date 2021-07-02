package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.model.MyLibraryDetailsDomain
import com.ditto.mylibrary.domain.model.ProductFilter
import io.reactivex.Single
import non_core.lib.Result

interface GetMylibraryData {
    fun invoke() : Single<Result<List<MyLibraryData>>>
    fun getUser(): Single<Result<LoginUser>>
    fun getPattern(get: Int): Single<Result<MyLibraryData>>
    //fun addProject( id : Int): Single<Any>
    fun removeProject(patternId: Int): Single<Any>
    fun completeProject(patternId: Int): Single<Any>
    fun getFilteredPatterns(createJson: ProductFilter): Single<Result<MyLibraryDetailsDomain>>
}

