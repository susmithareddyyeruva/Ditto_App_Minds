package com.ditto.splash.data

import com.ditto.splash.domain.DbRepository
import com.ditto.splash.domain.UpdateDbUseCase
import io.reactivex.Single
import javax.inject.Inject

class UpdateDbUseCaseImpl @Inject constructor(
    private val dbRepository: DbRepository
) : UpdateDbUseCase {

    override fun invoke(): Single<Any> {
        return dbRepository.getDbData()
    }
}