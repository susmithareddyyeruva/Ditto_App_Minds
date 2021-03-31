package com.ditto.login.data.api

import android.content.Context
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.error.LoginFetchError
import com.ditto.login.data.mapper.toDomain
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginRepository
import com.ditto.login.domain.LoginUser
import com.ditto.storage.data.database.UserDao
import core.network.Utility
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

/**
 * Concrete class of LoginRepository to expose Login Data from various sources (API, DB)
 */
class LoginRepositoryImpl @Inject constructor(
    private val loginService: @JvmSuppressWildcards LoginService,
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val loggerFactory: LoggerFactory
) : LoginRepository {
    @Inject
    lateinit var context: Context
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

    override fun loginUser(user: LoginUser): Single<Result<LoginUser>> {
        if (!Utility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return loginService.userLogin()
            .doOnSuccess { dbDataDao.deleteAndInsert(user.toDomain()) }
            .map { Result.withValue(it.toUserDomain()) }
            .onErrorReturn {
                Result.withError(
                    LoginFetchError("Error fetching data", it)
                )
            }
    }
}