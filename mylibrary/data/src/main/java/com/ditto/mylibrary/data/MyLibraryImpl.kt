package com.ditto.mylibrary.data

import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.GetMylibraryData
import com.ditto.mylibrary.domain.model.MyLibraryData
import javax.inject.Inject


class MyLibraryImpl @Inject constructor(
    private val myLibraryRepository: MyLibraryRepository
) : GetMylibraryData {
    override fun invoke(): Single<Result<List<MyLibraryData>>> {
        return myLibraryRepository.getMyLibraryData()
    }

    override fun getUser(): Single<Result<LoginUser>> {
        return myLibraryRepository.getUserData()
    }

    override fun getPattern(get:Int): Single<Result<MyLibraryData>> {
        return myLibraryRepository.getPatternData(get)
    }

    override fun removeProject(patternId: Int): Single<Any> {
        return myLibraryRepository.removePattern(patternId)
    }

    override fun completeProject(patternId: Int): Single<Any> {
        return myLibraryRepository.completeProject(patternId)
    }

//    override fun addProject(id : Int): Single<Any> {
//        return myLibraryRepository.addProject(id)
//    }

}