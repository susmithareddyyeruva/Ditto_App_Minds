package com.ditto.login.data.api

import com.ditto.login.domain.*
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetLoginDbUseCaseImpl @Inject constructor(
    private val loginRepository: LoginRepository
) : GetLoginDbUseCase {
    override fun invoke(): Single<Result<LoginUser>> {
        return loginRepository.getDbData()
    }

    override fun createUser(user: LoginUser): Single<Long> {
        return loginRepository.createUser(user)
    }

    override fun loginUserWithCredential(user: LoginInputData): Single<Result<LoginResultDomain>> {
        return loginRepository.loginUserWithCredential(user)
    }
}