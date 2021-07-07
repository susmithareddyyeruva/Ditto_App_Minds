package com.ditto.data

import android.content.Context
import android.util.Log
import com.ditto.data.api.MyLibraryService
import com.ditto.data.api.request.MyLibraryFilterRequestData
import com.ditto.data.api.request.OrderFilter
import com.ditto.data.api.request.ProductFilter
import com.ditto.data.error.HomeDataFetchError
import com.ditto.data.mapper.toDomain
import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.logger.LoggerFactory
import com.ditto.storage.data.database.UserDao
import core.appstate.AppState
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class MyLibraryRepositoryImpl @Inject constructor(
    private val homeService: @JvmSuppressWildcards MyLibraryService,
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val loggerFactory: LoggerFactory

): GetMyLibraryRepository {
    @Inject
    lateinit var context: Context
    val logger: com.ditto.logger.Logger by lazy {
        loggerFactory.create(MyLibraryRepositoryImpl::class.java.simpleName)
    }

    override fun getMyLibraryDetails(): Single<Result<MyLibraryDetailsDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return homeService.getHomeScreenDetails(MyLibraryFilterRequestData(OrderFilter(false,
        "mylibrary@gmail.com",true,false,false), ProductFilter()
        ),"Bearer "+ AppState.getToken()!!)
            .doOnSuccess {
                logger.d("*****FETCH HOME SUCCESS**")
            }
            .map {
                Result.withValue(it.toDomain())



            }
            .onErrorReturn {
                var errorMessage = "Error Fetching data"
                try {
                    logger.d("try block")
                } catch (e: Exception) {
                    Log.d("Catch", e.localizedMessage)
                    errorMessage = when (e) {
                        is UnknownHostException -> {
                            "Unknown host!"
                        }
                        is ConnectException -> {
                            "No Internet connection available !"
                        }
                        else -> {
                            "Error Fetching data!"
                        }
                    }
                }


                Result.withError(
                    HomeDataFetchError(errorMessage, it)
                )
            }
    }
}

