package com.ditto.mylibrary.data

import android.content.Context
import android.util.Log
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.data.api.MyLibraryFilterService
import com.ditto.mylibrary.data.api.TailornovaApiService
import com.ditto.mylibrary.data.error.FilterError
import com.ditto.mylibrary.data.mapper.toDomain
import com.ditto.mylibrary.data.mapper.toPatternIDDomain
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.model.AddFavouriteResultDomain
import com.ditto.mylibrary.domain.model.AllPatternsDomain
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.domain.model.FoldersResultDomain
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.domain.request.FolderRenameRequest
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.storage.data.database.OfflinePatternDataDao
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import core.OS
import core.CONNECTION_EXCEPTION
import core.ERROR_FETCH
import core.UNKNOWN_HOST_EXCEPTION
import core.USER_FIRST_NAME
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
import javax.inject.Inject

/**
 * Concrete class of MyLibraryRepository to expose MyLibrary Data from various sources (API, DB)
 */
class MyLibraryRepositoryImpl @Inject constructor(
    private val tailornovaApiService: @JvmSuppressWildcards TailornovaApiService,
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val patternsDao: @JvmSuppressWildcards PatternsDao,
    private val offlinePatternDataDao: @JvmSuppressWildcards OfflinePatternDataDao,
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
    override fun getMyLibraryData(filterRequestData: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.getAllPatternsPatterns(
            filterRequestData,
            "Bearer " + AppState.getToken()!!
        )
            .doOnSuccess {
                logger.d("*****FETCH PATTERNS SUCCESS**")
            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                try {
                    logger.d("try block")
                } catch (e: Exception) {
                    logger.d( e.localizedMessage)
                    errorMessage = when (e) {
                        is UnknownHostException -> {
                            USER_FIRST_NAME
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

    override fun getPatternData(get:String): Single<Result<PatternIdData>> {
        return tailornovaApiService.getPatternDetailsByDesignId(BuildConfig.TAILORNOVA_ENDURL+get, OS)
            .doOnSuccess {
                logger.d("*****Tailornova Success**")
                // patternType!= trial >> delete it
                offlinePatternDataDao.deletePatternsExceptTrial("trial", AppState.getCustID())
                offlinePatternDataDao.insertOfflinePatternData(it.toDomain())
                //insert to db
            }.map {
                Result.withValue(it)
            }
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
            }
    }

    override fun completeProject(patternId: String): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removePattern(patternId: String): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    
    override fun getMyLibraryFolderData(
        requestdata: GetFolderRequest,
        methodName: String
    ): Single<Result<FoldersResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.getFoldersList(
            requestdata, "Bearer " + AppState.getToken()!!,
            method = methodName
        )
            .doOnSuccess {
                logger.d("*****FETCH FOLDER LIST SUCCESS**")
            }
            .map {
                Result.withValue(it.toDomain())
            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                try {
                    logger.d("try block")
                } catch (e: Exception) {
                    Log.d("Catch", e.localizedMessage)
                    errorMessage = when (e) {
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

                logger.d(it.localizedMessage)
                Result.withError(
                    FilterError(errorMessage, it)
                )
            }
    }

    override fun addFolder(
        requestdata: FolderRequest,
        methodName: String
    ): Single<Result<AddFavouriteResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.addFolder(
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
                var errorMessage = ERROR_FETCH
                try {
                    logger.d("try block")
                } catch (e: Exception) {
                    Log.d("Catch", e.localizedMessage)
                    errorMessage = when (e) {
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

                logger.d(it.localizedMessage)
                Result.withError(
                    FilterError(errorMessage, it)
                )
            }
    }

    override fun renameFolder(
        renameRequest: FolderRenameRequest,
        methodName: String
    ): Single<Result<AddFavouriteResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return myLibraryService.renameFolder(
            renameRequest, "Bearer " + AppState.getToken()!!,
            method = methodName
        )
            .doOnSuccess {
                logger.d("*****methodName $methodName")
            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                try {
                    logger.d("try block")
                } catch (e: Exception) {
                    logger.d( e.localizedMessage)
                    errorMessage = when (e) {
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

                logger.d(it.localizedMessage)
                Result.withError(
                    FilterError(errorMessage, it)
                )
            }
    }

    override fun getOfflinePatternDetails(): Single<Result<List<ProdDomain>>> {
        return Single.fromCallable{
            val offlinePatternData = offlinePatternDataDao.getTailernovaData()
            if(offlinePatternData != null)
                Result.withValue(offlinePatternData.toDomain())
            else
                Result.withError(FilterError(""))
        }
    }

    override fun getOfflinePatternById(id: String): Single<Result<PatternIdData>> {
        return Single.fromCallable{
            val offlinePatternData = offlinePatternDataDao.getTailernovaDataByID(id)
            if(offlinePatternData != null)
                Result.withValue(offlinePatternData.toPatternIDDomain())
            else
                Result.withError(FilterError(""))
        }
    }


}
