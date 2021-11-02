package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.FolderRenameRequest
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
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
    fun getTrialPatterns(patternType:String):Single<Result<List<ProdDomain>>>
    fun getOfflinePatternById(id: String): Single<Result<PatternIdData>>

    //fun addProject(id : Int): Single<Any>
    fun getPatternData(get:Int): Single<Result<MyLibraryData>>
    fun getMyLibraryFolderData(requestdata: GetFolderRequest, methodName: String): Single<Result<FoldersResultDomain>>
    fun addFolder(requestdata: FolderRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
    fun renameFolder(createJson: FolderRenameRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
}