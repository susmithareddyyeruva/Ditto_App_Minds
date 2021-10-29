package com.ditto.data

import android.content.Context
import android.util.Log
import com.ditto.data.api.HomeApiService
import com.ditto.data.error.HomeDataFetchError
import com.ditto.data.mapper.toDomain
import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.request.MyLibraryFilterRequestData
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.domain.model.OfflinePatternData
import com.ditto.storage.data.database.OfflinePatternDataDao
import com.ditto.storage.data.database.UserDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.*
import core.di.Encrypt
import core.network.NetworkUtility
import core.ui.errors.CommonError
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class MyLibraryRepositoryImpl @Inject constructor(
    private val homeService: @JvmSuppressWildcards HomeApiService,
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val offlinePatternDataDao: @JvmSuppressWildcards OfflinePatternDataDao,
    private val loggerFactory: LoggerFactory

) : GetMyLibraryRepository {
    @Inject
    lateinit var context: Context
    val logger: com.ditto.logger.Logger by lazy {
        loggerFactory.create(MyLibraryRepositoryImpl::class.java.simpleName)
    }

    override fun getHomePatternsData(requestData: MyLibraryFilterRequestData): Single<Result<MyLibraryDetailsDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
            val input="$EN_USERNAME:$EN_PASSWORD"
       val encryptedKey= Encrypt.HMAC_SHA256(EN_KEY,input)
        return homeService.getHomeScreenDetails(requestData, "Basic "+encryptedKey)
            .doOnSuccess {
                if (!it.errorMsg.isNullOrEmpty()) {
                    logger.d("*****FETCH HOME SUCCESS 200 with Error **")
                    throw java.lang.Exception(it.errorMsg)
                } else {
                    logger.d("*****FETCH HOME SUCCESS**")
                }

            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    when (it.code()) {
                        400 -> {
                            val errorBody = it.response()!!.errorBody()!!.string()
                            Log.d("LoginError", errorBody)
                            val gson = Gson()
                            val type = object : TypeToken<CommonError>() {}.type
                            val errorResponse: CommonError? = gson.fromJson(errorBody, type)
                            errorMessage = errorResponse?.errorMsg ?: "Error Fetching data"
                            logger.d("onError: BAD REQUEST")

                        }
                        401 -> {
                            logger.d("onError: NOT AUTHORIZED")
                        }
                        403 -> {
                            logger.d("onError: FORBIDDEN")
                        }
                        404 -> {
                            logger.d("onError: NOT FOUND")
                        }
                        500 -> {
                            logger.d("onError: INTERNAL SERVER ERROR")
                        }
                        502 -> {
                            logger.d("onError: BAD GATEWAY")
                        }
                    }
                }
                else{
                    errorMessage = when (it) {
                        is UnknownHostException -> {
                            UNKNOWN_HOST_EXCEPTION
                        }
                        is ConnectException -> {
                            CONNECTION_EXCEPTION
                        }
                        else -> {
                            ERROR_FETCH
                        }
                    }
                }

                Result.withError(
                    HomeDataFetchError(errorMessage, it)
                )
            }
    }

    override fun getOfflinePatternDetails(): Single<Result<List<OfflinePatternData>>> {
        return Single.fromCallable {
            val offlinePatternData = offlinePatternDataDao.getTailernovaData()
            if (offlinePatternData != null)
                Result.withValue(offlinePatternData.toDomain())
            else
                Result.withError(HomeDataFetchError(""))
        }
    }
}

