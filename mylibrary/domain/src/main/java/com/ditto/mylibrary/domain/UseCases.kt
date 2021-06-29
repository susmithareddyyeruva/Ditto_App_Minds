package com.ditto.mylibrary.domain

import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.PatternIdResponse
import io.reactivex.Single
import non_core.lib.Result

interface GetMylibraryData {
    fun invoke() : Single<Result<List<MyLibraryData>>>
    fun getUser(): Single<Result<LoginUser>>
    fun getPattern(get: Int): Single<Result<PatternIdData>>
    //fun addProject( id : Int): Single<Any>
    fun removeProject(patternId: Int): Single<Any>
    fun completeProject(patternId: Int): Single<Any>
}

