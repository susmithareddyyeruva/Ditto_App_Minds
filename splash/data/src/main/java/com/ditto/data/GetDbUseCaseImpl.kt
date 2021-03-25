package com.ditto.data

import com.ditto.domain.DbRepository
import com.ditto.domain.GetDbDataUseCase
import com.ditto.login.domain.LoginUser
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