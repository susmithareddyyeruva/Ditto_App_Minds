package com.ditto.workspace.data

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.api.LoginRepositoryImpl
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import com.ditto.storage.data.database.WorkspaceDataDao
import com.ditto.workspace.data.api.GetWorkspaceService
import com.ditto.workspace.data.error.GetWorkspaceApiFetchError
import com.ditto.workspace.data.error.UpdateWorkspaceApiFetchError
import com.ditto.workspace.data.mapper.toDomain
import com.ditto.workspace.data.model.WSInputData
import com.ditto.workspace.domain.WorkspaceRepository
import com.ditto.workspace.domain.model.*
import com.google.gson.Gson
import core.CLIENT_ID_DEV
import core.SITE_ID
import core.appstate.AppState
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
    private val workspcaeDataDao: @JvmSuppressWildcards WorkspaceDataDao,
    private val getWorkspaceService: @JvmSuppressWildcards GetWorkspaceService,
    private val loggerFactory: LoggerFactory
) : WorkspaceRepository {

    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(LoginRepositoryImpl::class.java.simpleName)
    }

    /**
     * fetches PatternsData from local first. if not available locally, fetches from server
     */
    override fun getWorkspaceData(): Single<Result<List<PatternsData>>> {
        return Single.fromCallable {
            //fetch from local DB
            val data = patternsDao.getPatternsData()
            Result.withValue(data.toDomain())
        }
    }

    override fun insertData(patternsData: PatternsData): Single<Any> {
        return Single.fromCallable {
            patternsDao.upsert(patternsData.toDomain())
        }
    }

    override fun insertWorkspaceData(workspaceData: WorkspaceDataAPI): Single<Any> {
        return Single.fromCallable{
            workspcaeDataDao.insertWorkspcaeData(workspaceData.toDomain())
        }
    }

    override fun deleteAndInsert(id: Int, patternsData: PatternsData): Single<Any> {
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

    override fun getWorkspaceDataFromApi(): Single<Result<WorkspaceResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }

        return getWorkspaceService.getWorkspceDataFromApi(CLIENT_ID_DEV).doOnSuccess {
            logger.d("*****GetWorkspace Success**")
        }.map {
            Log.d("WorkspaceRepositoryImpl","${it.toString()}")
            Result.withValue(it.toDomain())
        }.onErrorReturn {
            var errorMessage = "Error Fetching data"
            try {
                logger.d("try block")
                val error = it as HttpException
                if (error != null) {
                    logger.d("Error get WorkspaceData")
                }
            } catch (e: Exception) {
                Log.d("Catch", e.localizedMessage)
                errorMessage = e.message.toString()
            }
            Result.withError(
                GetWorkspaceApiFetchError(errorMessage, it)
            )
        }
    }

    override fun updateWorkspaceDataFromApi(cTraceWorkSpacePatternInputData: CTraceWorkSpacePatternInputData): Single<Result<WSUpdateResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        val jsonobj = Gson().toJson(cTraceWorkSpacePatternInputData)
        Log.d("WorkspaceRepositoryImpl", "json object is: $jsonobj")

        val jsonString = jsonobj.toString()
        Log.d("WorkspaceRepositoryImpl", "json string is: $jsonString")

        /*val wsInputData = WSInputData("{\n" +
                "    \"c_traceWorkSpacePattern\": \"{\\\"tailornaovaDesignId\\\":\\\"\\\",\\\"selectedTab\\\":\\\"\\\",\\\"status\\\":\\\"\\\",\\\"numberOfCompletedPieces\\\":{\\\"garment\\\":11,\\\"lining\\\":4,\\\"interface\\\":5},\\\"patternPieces\\\":[{\\\"id\\\":0,\\\"isCompleted\\\":\\\"\\\"},{\\\"id\\\":0,\\\"isCompleted\\\":\\\"\\\"}],\\\"garmetWorkspaceItems\\\":[{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"},{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"}],\\\"liningWorkspaceItems\\\":[{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"}],\\\"interfaceWorkspaceItems\\\":[{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"}]}\"\n" +
                "}")*/

        val wsInputData = WSInputData(jsonString)

        return getWorkspaceService.updateWorkspaceDataFromApi(
            CLIENT_ID_DEV, SITE_ID, wsInputData,
            "Bearer "+AppState.getToken()!!
        ).doOnSuccess {
            logger.d("*****update Workspace Success**")
        }.map {
            Result.withValue(it.toDomain())
        }.onErrorReturn {
            var errorMessage = "Error Fetching data"
            try {
                logger.d("try block")
                val error = it as HttpException
                if (error != null) {
                    logger.d("Error update WorkspaceData")
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
}
