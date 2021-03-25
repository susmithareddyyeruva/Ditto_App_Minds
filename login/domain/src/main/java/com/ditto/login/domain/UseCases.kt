package com.ditto.login.domain

import io.reactivex.Single
import non_core.lib.Result


interface GetLoginDbUseCase {
    fun invoke(): Single<Result<LoginUser>>
    fun createUser(user: LoginUser): Single<Long>
}

