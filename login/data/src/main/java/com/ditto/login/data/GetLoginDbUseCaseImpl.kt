package com.ditto.login.data

import com.ditto.login.domain.GetLoginDbUseCase
import com.ditto.login.domain.LoginRepository
import com.ditto.login.domain.LoginUser
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

    override fun userLogin(user: LoginUser): Single<Result<LoginUser>> {
        return loginRepository.loginUser(user)
    }
}