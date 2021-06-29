package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.PatternIdResponse
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface MyLibraryRepository {
    fun getMyLibraryData(): Single<Result<List<MyLibraryData>>>
    fun getUserData(): Single<Result<LoginUser>>
    fun getPatternData(get:Int): Single<Result<PatternIdData>>
    fun completeProject(patternId:Int): Single<Any>
    fun removePattern(patternId: Int): Single<Any>
    //fun addProject(id : Int): Single<Any>
}