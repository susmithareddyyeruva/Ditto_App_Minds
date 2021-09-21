package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.AddFavouriteResultDomain
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.FoldersResultDomain
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.request.FolderRenameRequest
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result

interface MyLibraryUseCase {
    fun getPatterns(createJson: MyLibraryFilterRequestData) :  Single<Result<AllPatternsDomain>>
    fun getUser(): Single<Result<LoginUser>>
    fun getPatternDetails(get: Int): Single<Result<MyLibraryData>>
    fun invokeFolderList(request: GetFolderRequest, methodName: String) :  Single<Result<FoldersResultDomain>>
    fun addFolder(createJson: FolderRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
    fun renameFolder(createJson: FolderRenameRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>

}

