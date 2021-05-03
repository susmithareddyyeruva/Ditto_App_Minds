package com.ditto.splash.domain

import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result


interface GetDbDataUseCase {
    fun getUser(): Single<Result<LoginUser>>

    fun deleteDbUser(user: LoginUser):Single<Boolean>
}
interface UpdateDbUseCase {
    fun invoke(): Single<Any>
}

