package com.ditto.login.domain


import com.ditto.login.domain.model.LandingContentDomain
import com.ditto.login.domain.model.LoginInputData
import com.ditto.login.domain.model.LoginResultDomain
import com.ditto.login.domain.model.LoginUser
import io.reactivex.Single
import non_core.lib.Result

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface LoginRepository {
    fun getDbData(): Single<Result<LoginUser>>

    fun createUser(user: LoginUser): Single<Long>

    fun loginUserWithCredential(user: LoginInputData): Single<Result<LoginResultDomain>>

    fun deleteDbUser(user: String):Single<Boolean>

    fun getLandingDetails(): Single<Result<LandingContentDomain>>
}