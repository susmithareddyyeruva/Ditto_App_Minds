package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.FolderRenameRequest
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result

interface MyLibraryUseCase {
    fun getPatterns(filterRequestData: MyLibraryFilterRequestData) :  Single<Result<AllPatternsDomain>>
    fun getUser(): Single<Result<LoginUser>>
    fun getPattern(get: String,mannequinId:String): Single<Result<PatternIdData>>
    fun deletePattern(trial: String,custID: String,tailornovaDesignID: String): Single<Result<Boolean>>
    //fun addProject( id : Int): Single<Any>
    fun removeProject(patternId: String): Single<Any>
    fun completeProject(patternId: String): Single<Any>
    fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun invokeFolderList(createJson: MyLibraryFilterRequestData) :  Single<Result<AllPatternsDomain>>
    fun getOfflinePatternDetails(): Single<Result<List<ProdDomain>>>
    fun getTrialPatterns(patternType:String): Single<Result<List<ProdDomain>>>
    fun getAllPatternsInDB(): Single<Result<List<ProdDomain>>>
    fun getOfflinePatternById(id: String): Single<Result<PatternIdData>>
    fun getPatternDetails(get: Int): Single<Result<MyLibraryData>>
    fun invokeFolderList(request: GetFolderRequest, methodName: String) :  Single<Result<FoldersResultDomain>>
    fun addFolder(createJson: FolderRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
    fun renameFolder(createJson: FolderRenameRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
    fun insertTailornovaDetails(
        patternIdData: PatternIdData,
        orderNumber: String?,
        tailornovaDesignName: String?,
        prodSize: String?,
        status: String?,
        mannequinId: String?,
        mannequinName: String?,
        mannequin: List<MannequinDataDomain>?
    ): Single<Any>

}

