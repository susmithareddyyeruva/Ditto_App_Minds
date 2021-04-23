package com.ditto.login.data.api

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.error.LoginError
import com.ditto.login.data.error.LoginFetchError
import com.ditto.login.data.mapper.toDomain
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.data.model.LoginRequest
import com.ditto.login.domain.LoginInputData
import com.ditto.login.domain.LoginRepository
import com.ditto.login.domain.LoginResultDomain
import com.ditto.login.domain.LoginUser
import com.ditto.storage.data.database.UserDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.network.Utility
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import okhttp3.Credentials
import retrofit2.HttpException
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
            val dbData = dbDataDao.getUserData()
            Result.withValue(dbData.toUserDomain())
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

    override fun loginUserWithCredential(user: LoginInputData): Single<Result<LoginResultDomain>> {
        if (!Utility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        val loginRequest = LoginRequest("credentials")
        val basic =
            Credentials.basic(username = user.Username ?: "", password = user.Password ?: "")
        return loginService.loginWithCredential(
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            loginRequest,
            basic
        )
            .doOnSuccess {
                Log.d("Login", "*****Login Success**")
            }
            .map {
                Log.d("Login", "*****Login Success MAP**")
                Result.withValue(it.toUserDomain())

            }
            .onErrorReturn {
                val error = it as HttpException
                var errorMessage = "Error Fetching data"
                try {
                    val errorBody = error.response()!!.errorBody()!!.string()
                    Log.d("LoginError", errorBody)
                    val gson = Gson()
                    val type = object : TypeToken<LoginError>() {}.type
                    val errorResponse: LoginError? = gson.fromJson(errorBody, type)
                    errorMessage = errorResponse?.fault?.message ?: "Error Fetching data"
                    Log.d("LoginErrorResponse", errorMessage)
                } catch (e: Exception) {
                    Log.d("LoginErrorResponse", e.localizedMessage)
                }


                Result.withError(
                    LoginFetchError(errorMessage, it)
                )
            }


    }
}

