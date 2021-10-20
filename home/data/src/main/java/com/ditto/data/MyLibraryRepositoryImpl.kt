package com.ditto.data

import android.content.Context
import android.util.Log
import com.ditto.data.api.HomeApiService
import com.ditto.data.error.HomeDataFetchError
import com.ditto.data.mapper.offlinetoDomain
import com.ditto.data.mapper.toDomain
import com.ditto.data.mapper.toDomainn
import com.ditto.home.domain.GetMyLibraryRepository
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.request.MyLibraryFilterRequestData
import com.ditto.logger.LoggerFactory
import com.ditto.mylibrary.data.api.TailornovaApiService
import com.ditto.mylibrary.data.error.TrialPatternError
import com.ditto.mylibrary.data.mapper.toDomain
import com.ditto.mylibrary.domain.model.OfflinePatternData
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.storage.data.database.OfflinePatternDataDao
import com.ditto.storage.data.database.TraceDataDatabase
import com.ditto.storage.data.database.UserDao
import core.appstate.AppState
import core.lib.BuildConfig
import core.models.CommonApiFetchError
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.Executors
import javax.inject.Inject

class MyLibraryRepositoryImpl @Inject constructor(
    private val homeService: @JvmSuppressWildcards HomeApiService,
    private val tailornovaApiService: @JvmSuppressWildcards TailornovaApiService,
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
        return homeService.getHomeScreenDetails(requestData, "Bearer " + AppState.getToken()!!)
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
                var errorMessage = "Error Fetching data"
                errorMessage = when (it) {
                    is UnknownHostException -> {
                        "Unknown host!"
                    }
                    is ConnectException -> {
                        "No Internet connection available !"
                    }
                    else -> {
                        it.localizedMessage
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

    override fun fetchTailornovaTrialPatterns(): Single<Result<List<PatternIdData>>> {
        return tailornovaApiService.getTrialPatterns(BuildConfig.TAILORNOVA_ENDURL + "Android/trial")
            .doOnSuccess {
                logger.d("Tailornova Success")
                offlinePatternDataDao.insertOfflinePatternDataList(it.trial.toDomainn())
                //PatternIdData>>OfflinePatterns

                /*Executors.newSingleThreadExecutor()
                    .execute(Runnable {offlinePatternDataDao.insertOfflinePatternDataList(it.trial.toDomainn()) })*/
                Log.d("Tailornova", "insertofflinePatternsData complete: $it")
            }.map {
            it.trial?.let { it1 -> Result.withValue(it1) }
        }.onErrorReturn {
            var errorMessage = "Error fetching data"

            try {
                logger.d("try block")
                val error = it as HttpException
                if (error != null) {
                    logger.d("Error Tailornova")
                }
            } catch (e: Exception) {
                //logger.d("Catch",e.message.toString())
                errorMessage = e.message.toString()
            }
            Result.withError(
                CommonApiFetchError(errorMessage, it)
            )
        }
    }

    override fun getTrialPatterns(): Single<Result<List<ProdDomain>>> {
        return Single.fromCallable {
            val trialPatterns = offlinePatternDataDao.getListOfTrialPattern("Trial")
            if (trialPatterns != null)
                Result.withValue(trialPatterns.offlinetoDomain() )
            else
                Result.withError(TrialPatternError(""))
        }
    }


    /* return tailornovaApiService.getPatternDetailsByDesignId(

     .onErrorReturn {
         var errorMessage = "Error Fetching data"
         try {
             logger.d("try block")
             val error = it as HttpException
             if (error != null) {
                 logger.d("Error Tailornova")
             }
         } catch (e: Exception) {
             Log.d("Catch", e.localizedMessage)
             errorMessage = e.message.toString()
         }
         Result.withError(
             CommonApiFetchError(errorMessage, it)
         )
     }*/
}

