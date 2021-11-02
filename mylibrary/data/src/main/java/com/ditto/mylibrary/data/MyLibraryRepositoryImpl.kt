package com.ditto.mylibrary.data

import android.content.Context
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.data.api.MyLibraryFilterService
import com.ditto.mylibrary.data.api.TailornovaApiService
import com.ditto.mylibrary.data.error.FilterError
import com.ditto.mylibrary.data.error.TrialPatternError
import com.ditto.mylibrary.data.mapper.toDomain
import com.ditto.mylibrary.data.mapper.toPatternIDDomain
import com.ditto.mylibrary.domain.MyLibraryRepository
import com.ditto.mylibrary.domain.model.*
import com.ditto.mylibrary.domain.request.FolderRenameRequest
import com.ditto.mylibrary.domain.request.FolderRequest
import com.ditto.mylibrary.domain.request.GetFolderRequest
import com.ditto.mylibrary.domain.request.MyLibraryFilterRequestData
import com.ditto.storage.data.database.OfflinePatternDataDao
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import core.*
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
import javax.net.ssl.HttpsURLConnection


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
                if (!it.errorMsg.isNullOrEmpty()) {
                    logger.d("*****FETCH PATTERNS SUCCESS 200 with Error **")
                    throw java.lang.Exception(it.errorMsg)
                } else {
                    logger.d("*****FETCH PATTERNS SUCCESS**")
                }
            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    errorMessage = when (it.code()) {
                        HttpsURLConnection.HTTP_UNAUTHORIZED -> HTTP_UNAUTHORIZED4
                        HttpsURLConnection.HTTP_FORBIDDEN -> HTTP_FORBIDDEN
                        HttpsURLConnection.HTTP_INTERNAL_ERROR -> HTTP_INTERNAL_ERROR
                        HttpsURLConnection.HTTP_BAD_REQUEST -> HTTP_BAD_REQUEST
                        else -> ERROR_FETCH
                    }
                }else{
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

    override fun getPatternData(get: String): Single<Result<PatternIdData>> {
        return tailornovaApiService.getPatternDetailsByDesignId(
            BuildConfig.TAILORNOVA_ENDURL + get,
            OS
        )
            .doOnSuccess {
                logger.d("*****Tailornova Success**")
                // patternType!= trial >> delete it
                offlinePatternDataDao.deletePatternsExceptTrial("Trial", AppState.getCustID())
                offlinePatternDataDao.insertOfflinePatternData(it.toDomain())
                //insert to db
            }.map {
                Result.withValue(it)
            }
            .onErrorReturn {
                var errorMessage = "Error Fetching data"
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    errorMessage = when (it.code()) {
                        HttpsURLConnection.HTTP_UNAUTHORIZED -> HTTP_UNAUTHORIZED4
                        HttpsURLConnection.HTTP_FORBIDDEN -> HTTP_FORBIDDEN
                        HttpsURLConnection.HTTP_INTERNAL_ERROR -> HTTP_INTERNAL_ERROR
                        HttpsURLConnection.HTTP_BAD_REQUEST -> HTTP_BAD_REQUEST
                        else -> ERROR_FETCH
                    }
                }else{
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
                    CommonApiFetchError(errorMessage, it)
                )
            }
    }

    override fun getPatternData(get: Int): Single<Result<MyLibraryData>> {
        TODO("Not yet implemented")
    }

    override fun completeProject(patternId: String): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removePattern(patternId: String): Single<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFilteredPatterns(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        TODO("Not yet implemented")
    }

    override fun getMyLibraryFolderData(createJson: MyLibraryFilterRequestData): Single<Result<AllPatternsDomain>> {
        TODO("Not yet implemented")
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
                if (!it.errorMsg.isNullOrEmpty()) {
                    logger.d("*****FETCH FOLDER LIST SUCCESS 200 with Error **")
                    throw java.lang.Exception(it.errorMsg)
                } else {
                    logger.d("*****FETCH FOLDER LIST SUCCESS**")
                }
            }
            .map {
                Result.withValue(it.toDomain())
            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    errorMessage = when (it.code()) {
                        HttpsURLConnection.HTTP_UNAUTHORIZED -> HTTP_UNAUTHORIZED4
                        HttpsURLConnection.HTTP_FORBIDDEN -> HTTP_FORBIDDEN
                        HttpsURLConnection.HTTP_INTERNAL_ERROR -> HTTP_INTERNAL_ERROR
                        HttpsURLConnection.HTTP_BAD_REQUEST -> HTTP_BAD_REQUEST
                        else -> ERROR_FETCH
                    }
                }else{
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
                if (!it.errorMsg.isNullOrEmpty()) {
                    logger.d("*****ADD  FOLDER API SUCCESS 200 with Error **")
                    throw java.lang.Exception(it.errorMsg)
                } else {
                    logger.d("*****ADD  FOLDER API SUCCESS**")
                }
            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    errorMessage = when (it.code()) {
                        HttpsURLConnection.HTTP_UNAUTHORIZED -> HTTP_UNAUTHORIZED4
                        HttpsURLConnection.HTTP_FORBIDDEN -> HTTP_FORBIDDEN
                        HttpsURLConnection.HTTP_INTERNAL_ERROR -> HTTP_INTERNAL_ERROR
                        HttpsURLConnection.HTTP_BAD_REQUEST -> HTTP_BAD_REQUEST
                        else -> ERROR_FETCH
                    }
                }else{
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
                if (!it.errorMsg.isNullOrEmpty()) {
                    logger.d("*****RENAME  FOLDER API SUCCESS 200 with Error **")
                    throw java.lang.Exception(it.errorMsg)
                } else {
                    logger.d("*****RENAME  FOLDER API SUCCESS**")
                }

            }
            .map {
                Result.withValue(it.toDomain())


            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    errorMessage = when (it.code()) {
                        HttpsURLConnection.HTTP_UNAUTHORIZED -> HTTP_UNAUTHORIZED4
                        HttpsURLConnection.HTTP_FORBIDDEN -> HTTP_FORBIDDEN
                        HttpsURLConnection.HTTP_INTERNAL_ERROR -> HTTP_INTERNAL_ERROR
                        HttpsURLConnection.HTTP_BAD_REQUEST -> HTTP_BAD_REQUEST
                        else -> ERROR_FETCH
                    }
                }else{
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
                    FilterError(errorMessage, it)
                )
            }
    }

    override fun getOfflinePatternDetails(): Single<Result<List<ProdDomain>>> {
        return Single.fromCallable {
            val offlinePatternData = offlinePatternDataDao.getTailernovaData()
            if (offlinePatternData != null)
                Result.withValue(offlinePatternData.toDomain())
            else
                Result.withError(FilterError(""))
        }
    }

    override fun getTrialPatterns(patterntype:String): Single<Result<List<ProdDomain>>> {
        return Single.fromCallable {
            val trialPatterns = offlinePatternDataDao.getListOfTrialPattern(patterntype)
            if (trialPatterns != null)
                Result.withValue(trialPatterns.toDomain())
            else
                Result.withError(TrialPatternError(""))
        }
    }

    override fun getOfflinePatternById(id: String): Single<Result<PatternIdData>> {
        return Single.fromCallable {
            val offlinePatternData = offlinePatternDataDao.getTailernovaDataByID(id)
            if (offlinePatternData != null)
                Result.withValue(offlinePatternData.toPatternIDDomain())
            else
                Result.withError(FilterError(""))
        }
    }


}
