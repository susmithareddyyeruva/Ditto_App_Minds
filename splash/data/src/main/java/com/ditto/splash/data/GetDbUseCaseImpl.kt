package com.ditto.splash.data

import com.ditto.login.domain.model.LoginUser
import com.ditto.splash.domain.DbRepository
import com.ditto.splash.domain.GetDbDataUseCase
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetDbUseCaseImpl @Inject constructor(
    private val dbRepository: DbRepository
) : GetDbDataUseCase {

    override fun getUser(): Single<Result<LoginUser>> {
        return dbRepository.getUser()
    }
}