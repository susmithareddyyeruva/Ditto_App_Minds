package com.ditto.data

import android.content.Context
import android.util.Log
import com.ditto.data.api.HomeApiService
import com.ditto.data.error.HomeDataFetchError
import com.ditto.data.mapper.toDomain
import com.ditto.data.mapper.toDomainn
import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.model.OfflinePatternData
import com.ditto.home.domain.request.MyLibraryFilterRequestData
import com.ditto.logger.LoggerFactory
import com.ditto.storage.data.database.OfflinePatternDataDao
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
    private val homeService: @JvmSuppressWildcards HomeApiService,
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val offlinePatternDataDao: @JvmSuppressWildcards OfflinePatternDataDao,
    private val loggerFactory: LoggerFactory

): GetMyLibraryRepository {
    @Inject
    lateinit var context: Context
    val logger: com.ditto.logger.Logger by lazy {
        loggerFactory.create(MyLibraryRepositoryImpl::class.java.simpleName)
    }

    override fun getMyLibraryDetails(requestData: MyLibraryFilterRequestData): Single<Result<MyLibraryDetailsDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return homeService.getHomeScreenDetails(requestData,"Bearer "+ AppState.getToken()!!)
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

    override fun getOfflinePatternDetails(): Single<Result<List<OfflinePatternData>>> {
        return Single.fromCallable{
            val offlinePatternData = offlinePatternDataDao.getTailernovaData()
            if(offlinePatternData != null)
                Result.withValue(offlinePatternData.toDomain())
            else
                Result.withError(HomeDataFetchError(""))
        }
    }
}

