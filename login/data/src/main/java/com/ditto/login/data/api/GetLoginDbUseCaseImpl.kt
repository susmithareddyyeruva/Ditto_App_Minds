package com.ditto.login.data.api

import com.ditto.login.domain.LoginRepository
import com.ditto.login.domain.model.*
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

    override fun deleteDbUser(user: String): Single<Boolean> {
        return loginRepository.deleteDbUser(user)
    }

    override fun getLandingContentDetails(): Single<Result<LandingContentDomain>> {
        return loginRepository.getLandingDetails()
    }
}