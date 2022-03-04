package com.ditto.menuitems.data

import android.content.Context
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.data.api.AccountInfoService
import com.ditto.menuitems.data.error.AboutAppFetchError
import com.ditto.menuitems.data.error.AccountInfoFetchError
import com.ditto.menuitems.data.mapper.toDomain
import com.ditto.menuitems.domain.AccountInfoRepository
import com.ditto.menuitems.domain.model.AccountInfoDomain
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.CONNECTION_EXCEPTION
import core.ERROR_FETCH
import core.NO_SUCH_ELEMENT_EXCEPTION
import core.UNKNOWN_HOST_EXCEPTION
import core.network.NetworkUtility
import core.ui.errors.CommonError
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class AccountInfoRepositoryImpl @Inject constructor(
    private val accountInfoService: @JvmSuppressWildcards AccountInfoService,
    private val loggerFactory: LoggerFactory
) : AccountInfoRepository {

    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(AccountInfoRepositoryImpl::class.java.simpleName)
    }

    override fun deleteAccount(custNO: String?): Single<Result<AccountInfoDomain>> {
          if (!NetworkUtility.isNetworkAvailable(context)) {
              return Single.just(non_core.lib.Result.OnError(NoNetworkError()))
          }

        return accountInfoService.deleteAccountInfo(
            custNO ?: "").doOnSuccess {
            logger.d("**Delete Success**")
        }.map {
            logger.d("**Delete: on map ${it.toString()}")
            Result.withValue(it.toDomain())
        }.onErrorReturn {
            logger.d("**Delete: on Error ${it.toString()}")
            var errorMessage = ERROR_FETCH
            it.localizedMessage?.let {
                errorMessage = it
            }

            //logger.d(it.localizedMessage)
            if (it is HttpException) {
                when (it.code()) {
                    404 -> {
                        val errorBody = it.response()?.errorBody()?.string()
                        logger.d("Account Delete Info: $errorBody")
                        val gson = Gson()
                        val type = object : TypeToken<CommonError>() {}.type
                        val errorResponse: CommonError? = gson.fromJson(errorBody, type)
                        errorMessage = (errorResponse?.errorMsg ?: ERROR_FETCH)
                        logger.d("onError: BAD REQUEST")

                    }
                }
            } else {
                errorMessage = when (it) {
                    is UnknownHostException -> {
                        UNKNOWN_HOST_EXCEPTION
                    }
                    is ConnectException -> {
                        CONNECTION_EXCEPTION
                    }
                    is NoSuchElementException -> {
                        NO_SUCH_ELEMENT_EXCEPTION
                    }
                    else -> {
                        ERROR_FETCH
                    }
                }
            }

            Result.withError(
                AccountInfoFetchError(errorMessage, it)
            )
        }

    }


}