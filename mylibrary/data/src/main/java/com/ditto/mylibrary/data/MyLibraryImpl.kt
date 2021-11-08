package com.ditto.mylibrary.data

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.MyLibraryUseCase
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.FolderRenameRequest
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject


class MyLibraryImpl @Inject constructor(
    private val myLibraryRepository: MyLibraryRepository
) : MyLibraryUseCase {
    override fun getPatterns(filterRequestData: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        return myLibraryRepository.getMyLibraryData(filterRequestData)
    }

    override fun getUser(): Single<Result<LoginUser>> {
        return myLibraryRepository.getUserData()
    }

    override fun getPattern(get: String): Single<Result<PatternIdData>> {
        return myLibraryRepository.getPatternData(get)
    }

    override fun removeProject(patternId: String): Single<Any> {
        return myLibraryRepository.removePattern(patternId)
    }

    override fun completeProject(patternId: String): Single<Any> {
        return myLibraryRepository.completeProject(patternId)
    }

    override fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        TODO("Not yet implemented")
    }

    override fun getPatternDetails(get: Int): Single<Result<MyLibraryData>> {
        return myLibraryRepository.getPatternData(get)
    }

    override fun invokeFolderList(
        requestdata: GetFolderRequest,
        methodName: String
    ): Single<Result<FoldersResultDomain>> {
        return myLibraryRepository.getMyLibraryFolderData(requestdata, methodName)
    }

    override fun addFolder(
        createJson: FolderRequest,
        methodName: String
    ): Single<Result<AddFavouriteResultDomain>> {
        return myLibraryRepository.addFolder(createJson, methodName)
    }

    override fun renameFolder(
        createJson: FolderRenameRequest,
        methodName: String
    ): Single<Result<AddFavouriteResultDomain>> {
        return myLibraryRepository.renameFolder(createJson, methodName)
    }

    override fun invokeFolderList(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        return myLibraryRepository.getMyLibraryFolderData(createJson)
    }

    override fun getOfflinePatternDetails(): Single<Result<List<ProdDomain>>> {
        return myLibraryRepository.getOfflinePatternDetails()
    }

    override fun getTrialPatterns(): Single<Result<List<ProdDomain>>> {
        return  myLibraryRepository.getTrialPatterns()
    }

    override fun getOfflinePatternById(id: String): Single<Result<PatternIdData>> {
        return myLibraryRepository.getOfflinePatternById(id)
    }

}