package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface MyLibraryRepository {
    fun getMyLibraryData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getUserData(): Single<Result<LoginUser>>
    fun getPatternData(get:String): Single<Result<PatternIdData>>
    fun completeProject(patternId:String): Single<Any>
    fun removePattern(patternId: String): Single<Any>
    fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getMyLibraryFolderData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getOfflinePatternDetails(): Single<Result<List<ProdDomain>>>
    fun getOfflinePatternById(id: String): Single<Result<PatternIdData>>

    //fun addProject(id : Int): Single<Any>
}