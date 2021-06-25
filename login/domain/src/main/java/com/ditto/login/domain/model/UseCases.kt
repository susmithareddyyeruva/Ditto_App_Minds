package com.ditto.login.domain.model

import io.reactivex.Single
import non_core.lib.Result


interface GetLoginDbUseCase {
    fun invoke(): Single<Result<LoginUser>>
    fun createUser(user: LoginUser): Single<Long>
    fun loginUserWithCredential(user: LoginInputData): Single<Result<LoginResultDomain>>
    fun deleteDbUser(user: String):Single<Boolean>
    fun  getLandingContentDetails(): Single<Result<LandingContentDomain>>

}

