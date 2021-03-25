package com.ditto.login.data

import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.toDomain
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginRepository
import com.ditto.login.domain.LoginUser
import com.ditto.storage.data.database.UserDao
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import javax.inject.Inject

/**
 * Concrete class of LoginRepository to expose Login Data from various sources (API, DB)
 */
class LoginRepositoryImpl @Inject constructor(
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val loggerFactory: LoggerFactory
) : LoginRepository {

    val logger: Logger by lazy {
        loggerFactory.create(LoginRepositoryImpl::class.java.simpleName)
    }

    /**
     * fetches data from local store.
     */
    override fun getDbData(): Single<Result<LoginUser>> {
        return Single.fromCallable {
            val data = dbDataDao.getUserData()
            if (data != null)
                Result.withValue(data.toUserDomain())
            else
                Result.withValue(data.toUserDomain())
        }
    }

    /**
     * creates user data to local store.
     */
    override fun createUser(user: LoginUser): Single<Long> {
        logger.d("Create user")
        return dbDataDao.deleteAndInsert(user.toDomain())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}