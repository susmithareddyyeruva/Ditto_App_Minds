package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.AddFavouriteResult
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.request.FavouriteRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface MyLibraryRepository {
    fun getMyLibraryData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getUserData(): Single<Result<LoginUser>>
    fun getPatternData(get:Int): Single<Result<MyLibraryData>>
    fun completeProject(patternId:Int): Single<Any>
    fun removePattern(patternId: Int): Single<Any>
    fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getMyLibraryFolderData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun addtoFavourite(requestdata: FavouriteRequest): Single<Result<AddFavouriteResult>>

    //fun addProject(id : Int): Single<Any>
}