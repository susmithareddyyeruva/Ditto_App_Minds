package com.ditto.workspace.data

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.storage.data.database.OfflinePatternDataDao
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import com.ditto.storage.data.model.WorkspaceItemOffline
import com.ditto.workspace.data.api.GetWorkspaceService
import com.ditto.workspace.data.error.*
import com.ditto.workspace.data.mapper.toDomain
import com.ditto.workspace.data.mapper.toDomainn
import com.ditto.workspace.data.model.WSInputData
import com.ditto.workspace.domain.WorkspaceRepository
import com.ditto.workspace.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.SITE_ID
import core.appstate.AppState
import core.lib.BuildConfig
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Concrete class of WorkspaceRepository to expose Workspace Data from various sources (API, DB)
 */
class WorkspaceRepositoryImpl @Inject constructor(
    private val patternsDao: @JvmSuppressWildcards PatternsDao,
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val offlinePatternDataDao: @JvmSuppressWildcards OfflinePatternDataDao,
    private val getWorkspaceService: @JvmSuppressWildcards GetWorkspaceService,
    private val loggerFactory: LoggerFactory
) : WorkspaceRepository {

    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(WorkspaceRepositoryImpl::class.java.simpleName)
    }


    override fun insertData(patternsData: PatternsData): Single<Any> {
        return Single.fromCallable {
            patternsDao.upsert(patternsData.toDomain())
        }
    }

    override fun insertWorkspaceData(workspaceDataAPI: WorkspaceDataAPI): Single<Any> {
        return Single.fromCallable {
            //offlinePatternDataDao.insertOfflinePatternData(workspaceData.toDomain())
        }
    }

    override fun updateOfflineStorageData(
        tailornaovaDesignId: String?,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfPieces?,
        patternPieces: List<PatternPieceSFCCAPI>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        liningWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        interfaceWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        otherWorkspaceItems: MutableList<WorkspaceItemDomain>?,
        notes: String?
    ): Single<Int> {
        return Single.fromCallable {
            offlinePatternDataDao.updateOfflinePatternData(AppState.getCustID(),
                tailornaovaDesignId,
                selectedTab,
                status,
                numberOfCompletedPiece?.toDomain(),
                patternPieces?.map { it.toDomain() },
                garmetWorkspaceItems?.map { it.toDomain() } as MutableList<WorkspaceItemOffline>,
                liningWorkspaceItems?.map { it.toDomain() } as MutableList<WorkspaceItemOffline>,
                interfaceWorkspaceItems?.map { it.toDomain() } as MutableList<WorkspaceItemOffline>,
                otherWorkspaceItems?.map { it.toDomain()} as MutableList<WorkspaceItemOffline>,
                notes)
        }
    }

    override fun deleteAndInsert(id: String, patternsData: PatternsData): Single<Any> {
        return Single.fromCallable {
            patternsDao.deletePatternsData(id)
        }.flatMap { patternsDao.insertPatternsData(patternsData.toDomain()) }
    }

    override fun getUserData(): Single<Result<LoginUser>> {
        return Single.fromCallable {
            val data = dbDataDao.getUserData()
            if (data != null)
                Result.withValue(data.toUserDomain())
            else
                Result.withValue(LoginUser(""))
        }
    }

    override fun getPatternDataByID(id: Int): Single<Result<PatternsData>> {

        return Single.fromCallable {
            val patternsData = patternsDao.getPatternDataByID(id)
            if (patternsData != null)
                Result.withValue(patternsData.toDomain())
            else
                Result.withError(GetPatternDataError(""))
        }
    }

    override fun getTailernovaDataByID(id: String): Single<Result<OfflinePatternData>> {
        return Single.fromCallable {
            val offlinePatternData = if (AppState.getIsLogged()
            ) {
                offlinePatternDataDao.getTailernovaDataByID(id, AppState.getCustID())
            } else {
                offlinePatternDataDao.getTailernovaDataByIDTrial(id)
            }
            if (!AppState.getIsLogged()) {
                // To show blank Workspace for offline users
                offlinePatternData.garmetWorkspaceItemOfflines = emptyArray<WorkspaceItemOffline>().toMutableList()
                offlinePatternData.liningWorkspaceItemOfflines = emptyArray<WorkspaceItemOffline>().toMutableList()
                offlinePatternData.interfaceWorkspaceItemOfflines = emptyArray<WorkspaceItemOffline>().toMutableList()
                offlinePatternData.otherWorkspaceItemOfflines = emptyArray<WorkspaceItemOffline>().toMutableList()
            }
            if (offlinePatternData != null)
                Result.withValue(offlinePatternData.toDomainn())
            else
                Result.withError(GetOfflinePatternDataError(""))
        }
    }

    override fun getWorkspaceDataFromApi(id: String): Single<Result<WorkspaceDataAPI>> {
        logger.d("WSISSUE >> id: $id")
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }

        return getWorkspaceService.getWorkspceDataFromApi(id,BuildConfig.CLIENT_ID).doOnSuccess {
            logger.d("*****GetWorkspace Success**")
        }.map {
            Log.d(
                "WorkspaceRepositoryImpl",
                "getWorkspaceDataFromApi ${it.c_traceWorkSpacePattern.toString()}"
            )
            Result.withValue(it.c_traceWorkSpacePattern.toDomain())
        }.onErrorReturn {
            var errorMessage = "Error Fetching data"
            try {
                logger.d("getWorkspaceDataFromApi try block123 ")
                val error = it as HttpException
                if (error != null) {
                    val errorBody = error.response()!!.errorBody()!!.string()
                    Log.d("GetWorkspace Error", errorBody)
                    val gson = Gson()
                    val type = object : TypeToken<GetWorkspaceApiResponseFetchError>() {}.type
                    val errorResponse: GetWorkspaceApiResponseFetchError =
                        gson.fromJson(errorBody, type)
                    errorMessage = errorResponse.fault.message
                    logger.d("Error get WorkspaceData >>>>>>> $errorMessage")
                }
            } catch (e: Exception) {
                Log.d("Catch", e.localizedMessage + e.message)
                errorMessage = e.message.toString()
            }
            Result.withError(
                GetWorkspaceApiFetchError(errorMessage, it)
            )
        }
    }

    override fun updateWorkspaceDataFromApi(
        id: String,
        workspaceDataAPI: WorkspaceDataAPI
    ): Single<Result<WSUpdateResultDomain>> {
        logger.d("WSISSUE >> id: $id")
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        val jsonobj = Gson().toJson(workspaceDataAPI)
        Log.d(
            "WorkspaceRepositoryImpl",
            "updateWorkspaceDataFromApi ${jsonobj}"
        )
        val jsonString = jsonobj.toString()
        val wsInputData = WSInputData(jsonString)

        return getWorkspaceService.updateWorkspaceDataFromApi(
            id,
            BuildConfig.CLIENT_ID, SITE_ID, wsInputData,
            "Bearer " + AppState.getToken()!!
        ).doOnSuccess {
            logger.d("**Update Workspace Success**")
        }.map {
            Result.withValue(it.toDomain())
        }.onErrorReturn {
            var errorMessage = "Error Fetching data"
            try {
                logger.d("try block updateWorkspaceDataFromApi")
                val error = it as HttpException
                if (error != null) {
                    val errorBody = error.response()!!.errorBody()!!.string()
                    Log.d("GetWorkspace Error", errorBody)
                    val gson = Gson()
                    val type = object : TypeToken<GetWorkspaceApiResponseFetchError>() {}.type
                    val errorResponse: GetWorkspaceApiResponseFetchError =
                        gson.fromJson(errorBody, type)
                    errorMessage = errorResponse.fault.message
                    logger.d("Error get WorkspaceData >>>>>>> $errorMessage")
                }
            } catch (e: Exception) {
                Log.d("Catch", e.localizedMessage)
                errorMessage = e.message.toString()
            }
            Result.withError(
                UpdateWorkspaceApiFetchError(errorMessage, it)
            )
        }
    }

    override fun createWorkspaceDataFromApi(
        id: String,
        workspaceDataAPI: WorkspaceDataAPI
    ): Single<Result<WSUpdateResultDomain>> {
        logger.d("WSISSUE >> id: $id")
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        val jsonobj = Gson().toJson(workspaceDataAPI)
        Log.d("WorkspaceRepositoryImpl", "createWorkspaceDataFromApi >>json object is: $jsonobj")

        val jsonString = jsonobj.toString()
        Log.d("WorkspaceRepositoryImpl", "createWorkspaceDataFromApi >>json string is: $jsonString")

        val wsInputData = WSInputData(jsonString)

        return getWorkspaceService.createWorkspaceDataFromApi(
            id,
            BuildConfig.CLIENT_ID, SITE_ID, wsInputData,
            "Bearer " + AppState.getToken()!!
        ).doOnSuccess {
            logger.d("*****Create Workspace Success**")
        }.map {
            Result.withValue(it.toDomain())
        }.onErrorReturn {
            var errorMessage = "Error Fetching data"
            try {
                logger.d("try block createWorkspaceDataFromApi")
                val error = it as HttpException
                if (error != null) {
                    logger.d("Error createWorkspaceDataFromApi")
                }
            } catch (e: Exception) {
                Log.d("Catch", e.localizedMessage)
                errorMessage = e.message.toString()
            }
            Result.withError(
                CreateWorkspaceAPIError(errorMessage, it)
            )
        }
    }
}
