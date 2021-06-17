package com.ditto.login.data.api

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.error.LandingContentFetchError
import com.ditto.login.data.error.LoginError
import com.ditto.login.data.error.LoginFetchError
import com.ditto.login.data.mapper.toDomain
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.data.model.LoginRequest
import com.ditto.login.domain.LoginRepository
import com.ditto.login.domain.model.LandingContentDomain
import com.ditto.login.domain.model.LoginInputData
import com.ditto.login.domain.model.LoginResultDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.storage.data.database.UserDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.CLIENT_ID
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

    override fun loginUserWithCredential(user: LoginInputData): Single<Result<LoginResultDomain>> {
        if (!Utility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        val loginRequest = LoginRequest("credentials")
        val basic =
            Credentials.basic(username = user.Username ?: "", password = user.Password ?: "")
        return loginService.loginWithCredential(
            CLIENT_ID,
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
                var errorMessage = "Error Fetching data"
                try {
                    Log.d("Try", "try block")
                    val error = it as HttpException
                    if (error!=null) {
                        val errorBody = error.response()!!.errorBody()!!.string()
                        Log.d("LoginError", errorBody)
                        val gson = Gson()
                        val type = object : TypeToken<LoginError>() {}.type
                        val errorResponse: LoginError? = gson.fromJson(errorBody, type)
                        errorMessage = errorResponse?.fault?.message ?: "Error Fetching data"
                        Log.d("LoginErrorResponse", errorMessage)
                    }
                } catch (e: Exception) {
                    Log.d("Catch", e.localizedMessage)
                    errorMessage = e.message.toString()


                }


                Result.withError(
                    LoginFetchError(errorMessage, it)
                )
            }



    }

    override fun deleteDbUser(user: String): Single<Boolean> {
        return dbDataDao.deleteUserDataInfo(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getLandingDetails(): Single<Result<LandingContentDomain>> {
        if (!Utility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }else{
            return  loginService.getLandingContentDetails(CLIENT_ID)
                .doOnSuccess {
                    Log.d("Landing Content", "***** Success**")
                }
                .map {
                    Result.withValue(it.toDomain())

                }
                .onErrorReturn {
                    var errorMessage = "Error Fetching Landing Content"
                    Log.d("Try", "try block")


                    Result.withError(
                        LandingContentFetchError(errorMessage, it)
                    )
                }
        }
        }

}

