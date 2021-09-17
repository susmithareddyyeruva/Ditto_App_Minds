package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.AddFavouriteResultDomain
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.FoldersResultDomain
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result

interface GetMylibraryData {
    fun invoke(createJson: MyLibraryFilterRequestData) :  Single<Result<AllPatternsDomain>>
    fun getUser(): Single<Result<LoginUser>>
    fun getPattern(get: Int): Single<Result<MyLibraryData>>
    //fun addProject( id : Int): Single<Any>
    fun removeProject(patternId: Int): Single<Any>
    fun completeProject(patternId: Int): Single<Any>
    fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun invokeFolderList(request: GetFolderRequest, methodName: String) :  Single<Result<FoldersResultDomain>>
    fun addFolder(createJson: FolderRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
}

