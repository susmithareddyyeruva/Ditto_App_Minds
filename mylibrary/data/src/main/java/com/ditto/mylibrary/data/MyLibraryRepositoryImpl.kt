package com.ditto.mylibrary.data

import android.content.Context
import android.util.Log
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.data.api.MyLibraryFilterService
import com.ditto.mylibrary.data.error.FilterError
import com.ditto.mylibrary.data.mapper.toDomain
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.model.AddFavouriteResult
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.request.FavouriteRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import core.appstate.AppState
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Concrete class of MyLibraryRepository to expose MyLibrary Data from various sources (API, DB)
 */
class MyLibraryRepositoryImpl @Inject constructor(
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val patternsDao: @JvmSuppressWildcards PatternsDao,
    private val myLibraryService: @JvmSuppressWildcards MyLibraryFilterService,
    private val loggerFactory: LoggerFactory
) : MyLibraryRepository {
    @Inject
    lateinit var context: Context
    val logger: com.ditto.logger.Logger by lazy {
        loggerFactory.create(MyLibraryRepositoryImpl::class.java.simpleName)
    }

    /**
     * fetches data from local store first. if not available locally, fetches from server
     */
    override fun getMyLibraryData(request: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.getAllPatternsPatterns(request, "Bearer " + AppState.getToken()!!)
            .doOnSuccess {
                logger.d("*****FETCH FILTER SUCCESS**")
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

                logger.d(it.localizedMessage)
                Result.withError(
                    FilterError(errorMessage, it)
                )
            }
    }


    /**
     * creates user data to local store.
     */
    override fun getUserData(): Single<Result<LoginUser>> {
        return Single.fromCallable {
            val data = dbDataDao.getUserData()
            if (data != null)
                Result.withValue(data.toUserDomain())
            else
                Result.withValue(LoginUser(""))
        }
    }

    override fun getPatternData(get: Int): Single<Result<MyLibraryData>> {
        return Single.fromCallable {
            val data = patternsDao.getPatternDataByID(get)
            Result.withValue(data.toDomain())
        }
    }

    override fun completeProject(patternId: Int): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removePattern(patternId: Int): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getFilteredPatterns(request: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.getFilterredPatterns(
            request, "Bearer " + AppState.getToken()!!
        )
            .doOnSuccess {
                logger.d("*****FETCH FILTER SUCCESS**")
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
                logger.d(it.localizedMessage)

                Result.withError(
                    FilterError(errorMessage, it)
                )
            }

    }

    override fun getMyLibraryFolderData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.getFoldersList(createJson, "Bearer " + AppState.getToken()!!)
            .doOnSuccess {
                logger.d("*****FETCH FILTER SUCCESS**")
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

                logger.d(it.localizedMessage)
                Result.withError(
                    FilterError(errorMessage, it)
                )
            }
    }

    override fun addtoFavourite(requestdata: FavouriteRequest, methodName: String): Single<Result<AddFavouriteResult>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.addToFavourite(
            requestdata, "Bearer " + AppState.getToken()!!,
            method = methodName
        )
            .doOnSuccess {
                logger.d("*****methodName $methodName")
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

                logger.d(it.localizedMessage)
                Result.withError(
                    FilterError(errorMessage, it)
                )
            }
    }


//    override fun addProject(
//        id: Int,
//        dndOnboarding: Boolean,
//        isLaterClicked: Boolean
//    ): Single<Any> {
//        return Single.fromCallable {
//            dbDataDao.updateDndOnboarding(id,dndOnboarding, isLaterClicked)
//        }
//    }
}
