package com.ditto.mylibrary.data

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.model.AddFavouriteResult
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.request.FavouriteRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject


class MyLibraryImpl @Inject constructor(
    private val myLibraryRepository: MyLibraryRepository
) : GetMylibraryData {
    override fun invoke(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        return myLibraryRepository.getMyLibraryData(createJson)
    }

    override fun getUser(): Single<Result<LoginUser>> {
        return myLibraryRepository.getUserData()
    }

    override fun getPattern(get: Int): Single<Result<MyLibraryData>> {
        return myLibraryRepository.getPatternData(get)
    }

    override fun removeProject(patternId: Int): Single<Any> {
        return myLibraryRepository.removePattern(patternId)
    }

    override fun completeProject(patternId: Int): Single<Any> {
        return myLibraryRepository.completeProject(patternId)
    }

    override fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        return myLibraryRepository.getFilteredPatterns(createJson)
    }

    override fun invokeFolderList(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        return myLibraryRepository.getMyLibraryFolderData(createJson)
    }

    override fun addFavourite(createJson: FavouriteRequest): Single<Result<AddFavouriteResult>> {
        return myLibraryRepository.addtoFavourite(createJson)
    }
}