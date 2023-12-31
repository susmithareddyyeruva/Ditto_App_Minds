package com.ditto.splash.domain

import com.ditto.login.domain.model.LoginUser
import io.reactivex.Single
import non_core.lib.Result


interface GetDbDataUseCase {
    fun getUser(): Single<Result<LoginUser>>
}
interface UpdateDbUseCase {
    fun invoke(): Single<Any>
}

