package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.AddFavouriteResultDomain
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.FoldersResultDomain
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
    fun getMyLibraryFolderData(requestdata: FavouriteRequest, methodName: String): Single<Result<FoldersResultDomain>>
    fun addtoFavourite(requestdata: FavouriteRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>

    //fun addProject(id : Int): Single<Any>
}