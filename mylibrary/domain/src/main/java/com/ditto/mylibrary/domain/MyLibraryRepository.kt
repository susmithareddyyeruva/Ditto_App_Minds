package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.*
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface MyLibraryRepository {
    fun getMyLibraryData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getUserData(): Single<Result<LoginUser>>
    fun getPatternData(get:String,mannequinId:String): Single<Result<PatternIdData>>
    fun deletePattern(trial: String,custID: String,tailornovaDesignID: String): Single<Result<Boolean>>
    fun completeProject(patternId:String): Single<Any>
    fun removePattern(patternId: String): Single<Any>
    fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getMyLibraryFolderData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>>
    fun getOfflinePatternDetails(): Single<Result<List<ProdDomain>>>
    fun getTrialPatterns(patternType:String):Single<Result<List<ProdDomain>>>
    fun getAllPatternsInDB():Single<Result<List<ProdDomain>>>
    fun getOfflinePatternById(id: String): Single<Result<PatternIdData>>
    fun insertTailornovaDetails(
        patternIdData: PatternIdData,
        orderNumber: String?,
        tailornovaDesignName: String?,
        prodSize: String?,
        status: String?,
        mannequinId: String?,
        mannequinName: String?,
        mannequin: List<MannequinDataDomain>?,
        patternType:String?,
        lastDateOfModification: String?,
        selectedViewCupStyle: String?,
        yardagePdfUrl: String?,
        productId: String?
    ): Single<Any>
    //fun addProject(id : Int): Single<Any>
    fun getPatternData(get:Int): Single<Result<MyLibraryData>>
    fun getMyLibraryFolderData(requestdata: GetFolderRequest, methodName: String): Single<Result<FoldersResultDomain>>
    fun addFolder(requestdata: FolderRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
    fun renameFolder(createJson: FolderRenameRequest, methodName: String): Single<Result<AddFavouriteResultDomain>>
    fun getThirdPartyPatternData(requestData: ThirdPartyDataRequest) : Single<Result<ThirdPartyDomain>>
}