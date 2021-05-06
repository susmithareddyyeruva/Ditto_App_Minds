package com.ditto.splash.domain


import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface DbRepository {
    fun getDbData(): Single<Any>
    fun getUser(): Single<Result<LoginUser>>
}