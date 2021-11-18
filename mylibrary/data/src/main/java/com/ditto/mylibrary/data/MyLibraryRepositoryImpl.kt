package com.ditto.mylibrary.data

import android.content.Context
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.mylibrary.data.api.MyLibraryFilterService
import com.ditto.mylibrary.data.api.TailornovaApiService
import com.ditto.mylibrary.data.error.FilterError
import com.ditto.mylibrary.data.error.PatternDBError
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.*
import core.appstate.AppState
import core.di.EncodeDecodeUtil
import core.lib.BuildConfig
import core.models.CommonApiFetchError
import core.network.NetworkUtility
import core.ui.errors.CommonError
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
        val input = "$EN_USERNAME:$EN_PASSWORD"
        val key=EncodeDecodeUtil.decodeBase64(AppState.getKey())
        val encryptedKey = EncodeDecodeUtil.HMAC_SHA256(key, input)
        return myLibraryService.getAllPatternsPatterns(
            filterRequestData,
            "Basic " + encryptedKey
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
                    when (it.code()) {
                        400 -> {
                            val errorBody = it.response()!!.errorBody()!!.string()
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
                } else {
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

    override fun getPatternData(designeID: String): Single<Result<PatternIdData>> {
        return tailornovaApiService.getPatternDetailsByDesignId(
            BuildConfig.TAILORNOVA_ENDURL + designeID,
            OS
        )
            .doOnSuccess {
                logger.d("*****Tailornova Success**")
                // patternType!= trial >> delete it
                offlinePatternDataDao.deletePatternsExceptTrial("Trial", AppState.getCustID(),designeID)
            }.map {
                Result.withValue(it)
            }
            .onErrorReturn {
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    when (it.code()) {
                        400 -> {
                            val errorBody = it.response()!!.errorBody()!!.string()
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
                } else {
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
        val input = "$EN_USERNAME:$EN_PASSWORD"
        val key=EncodeDecodeUtil.decodeBase64(AppState.getKey())
        val encryptedKey = EncodeDecodeUtil.HMAC_SHA256(key, input)
        return myLibraryService.getFoldersList(
            requestdata, "Basic " + encryptedKey,
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
                ERROR_FETCH = it.localizedMessage
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    when (it.code()) {
                        400 -> {
                            val errorBody = it.response()!!.errorBody()!!.string()
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
                } else {
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
        val input = "$EN_USERNAME:$EN_PASSWORD"
        val key=EncodeDecodeUtil.decodeBase64(AppState.getKey())
        val encryptedKey = EncodeDecodeUtil.HMAC_SHA256(key, input)
        return myLibraryService.addFolder(
            requestdata, "Basic " + encryptedKey,
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
                ERROR_FETCH = it.localizedMessage
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    when (it.code()) {
                        400 -> {
                            val errorBody = it.response()!!.errorBody()!!.string()
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
                } else {
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
        val input = "$EN_USERNAME:$EN_PASSWORD"
        val key=EncodeDecodeUtil.decodeBase64(AppState.getKey())
        val encryptedKey = EncodeDecodeUtil.HMAC_SHA256(key, input)
        return myLibraryService.renameFolder(
            renameRequest, "Basic " + encryptedKey,
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
                ERROR_FETCH = it.localizedMessage
                var errorMessage = ERROR_FETCH
                logger.d(it.localizedMessage)
                if (it is HttpException) {
                    when (it.code()) {
                        400 -> {
                            val errorBody = it.response()!!.errorBody()!!.string()
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
                } else {
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


                logger.d(it.localizedMessage)
                Result.withError(
                    FilterError(errorMessage, it)
                )
            }
    }

    override fun getOfflinePatternDetails(): Single<Result<List<ProdDomain>>> {
        return Single.fromCallable {
            val offlinePatternData = offlinePatternDataDao.getAllPatterns()
            if (offlinePatternData != null)
                Result.withValue(offlinePatternData.toDomain())
            else
                Result.withError(FilterError(""))
        }
    }

    override fun getTrialPatterns(patterntype: String): Single<Result<List<ProdDomain>>> {
        return Single.fromCallable {
            val trialPatterns = offlinePatternDataDao.getListOfTrialPattern(patterntype)
            if (trialPatterns != null)
                Result.withValue(trialPatterns.toDomain())
            else
                Result.withError(TrialPatternError(""))
        }
    }

    override fun getAllPatternsInDB(): Single<Result<List<ProdDomain>>> {
        return Single.fromCallable {
            val patterns = offlinePatternDataDao.getAllPatterns()

            if (patterns != null)
                Result.withValue(patterns.toDomain())
            else
                Result.withError(PatternDBError(""))
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

    override fun insertTailornovaDetails(patternIdData: PatternIdData,orderNumber:String?): Single<Any> {
        return Single.fromCallable {
            val i = offlinePatternDataDao.upsert(patternIdData.toDomain(orderNumber))}
    }

}
