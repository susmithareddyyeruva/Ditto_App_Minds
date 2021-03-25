package com.ditto.login.domain


import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface LoginRepository {
    fun getDbData(): Single<Result<LoginUser>>

    fun createUser(user: LoginUser): Single<Long>
}