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
import com.ditto.workspace.data.api.GetWorkspaceService
import com.ditto.workspace.data.error.GetWorkspaceApiFetchError
import com.ditto.workspace.data.error.UpdateWorkspaceApiFetchError
import com.ditto.workspace.data.mapper.toDomain
import com.ditto.workspace.data.model.*
import com.ditto.workspace.domain.WorkspaceRepository
import com.ditto.workspace.domain.model.PatternsData
import com.ditto.workspace.domain.model.WSUpdateResultDomain
import com.ditto.workspace.domain.model.WorkspaceResultDomain
import com.google.gson.Gson
import core.CLIENT_ID
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

        return getWorkspaceService.getWorkspceDataFromApi(CLIENT_ID).doOnSuccess {
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

    override fun updateWorkspaceDataFromApi(): Single<Result<WSUpdateResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        val numberOfCompletedPiece =
            NumberOfCompletedPieceInputData(garment = 33, lining = 13, interfacee = 13)

        val patternPieces = ArrayList<PatternPieceInputData>()
        val patternInputData = PatternPieceInputData(id = 1, isCompleted = "true")
        patternPieces.add(patternInputData)
        val patternInputData2 = PatternPieceInputData(id = 2, isCompleted = "true")
        patternPieces.add(patternInputData2)

        //var garmetWorkspaceItems: List<WorkspaceItemInputData> = emptyList()
        val garmetWorkspaceItems = ArrayList<WorkspaceItemInputData>()
        val garmentWorkspaceItemInputData = WorkspaceItemInputData(id=1,patternPiecesId = 1,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.10",pivotX = "1",pivotY = "2",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "2")

        val garmentWorkspaceItemInputData1 = WorkspaceItemInputData(id=1,patternPiecesId = 1,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.10",pivotX = "1",pivotY = "2",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "2")
        garmetWorkspaceItems.add(garmentWorkspaceItemInputData)
        garmetWorkspaceItems.add(garmentWorkspaceItemInputData1)

        val liningWorkspaceItems = ArrayList<WorkspaceItemInputData>()
        val liningWorkspaceItemInputData = WorkspaceItemInputData(id=1,patternPiecesId = 1,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.10",pivotX = "1",pivotY = "2",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "2")
        liningWorkspaceItems.add(liningWorkspaceItemInputData)

        val interfaceWorkspaceItem = ArrayList<WorkspaceItemInputData>()

        val interfaceWorkspaceItemInputData = WorkspaceItemInputData(id=1,patternPiecesId = 1,
            isCompleted = "true",xcoordinate = "0.10",ycoordinate = "0.10",pivotX = "1",pivotY = "2",
            transformA = "1",transformD = "1",rotationAngle = "10",isMirrorH ="true",isMirrorV = "10",
            showMirrorDialog = "true",currentSplicedPieceNo = "2")
        interfaceWorkspaceItem.add(interfaceWorkspaceItemInputData)

        val cTraceWorkSpacePatternInputData = CTraceWorkSpacePatternInputData(tailornaovaDesignId =" " ,selectedTab ="" ,status = "",
            numberOfCompletedPiece = numberOfCompletedPiece,patternPieces,garmetWorkspaceItems = garmetWorkspaceItems,
            liningWorkspaceItems = liningWorkspaceItems,interfaceWorkspaceItem =interfaceWorkspaceItem)

        val jsonobj = Gson().toJson(cTraceWorkSpacePatternInputData)
        Log.d("WorkspaceRepositoryImpl", "json object is: $jsonobj")

        val jsonString = jsonobj.toString()
        Log.d("WorkspaceRepositoryImpl", "json string is: $jsonString")

        /*val wsInputData = WSInputData("{\n" +
                "    \"c_traceWorkSpacePattern\": \"{\\\"tailornaovaDesignId\\\":\\\"\\\",\\\"selectedTab\\\":\\\"\\\",\\\"status\\\":\\\"\\\",\\\"numberOfCompletedPieces\\\":{\\\"garment\\\":11,\\\"lining\\\":4,\\\"interface\\\":5},\\\"patternPieces\\\":[{\\\"id\\\":0,\\\"isCompleted\\\":\\\"\\\"},{\\\"id\\\":0,\\\"isCompleted\\\":\\\"\\\"}],\\\"garmetWorkspaceItems\\\":[{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"},{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"}],\\\"liningWorkspaceItems\\\":[{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"}],\\\"interfaceWorkspaceItems\\\":[{\\\"id\\\":0,\\\"patternPiecesId\\\":0,\\\"isCompleted\\\":\\\"\\\",\\\"xcoordinate\\\":\\\"\\\",\\\"ycoordinate\\\":\\\"\\\",\\\"pivotX\\\":\\\"\\\",\\\"pivotY\\\":\\\"\\\",\\\"transformA\\\":\\\"\\\",\\\"transformD\\\":\\\"\\\",\\\"rotationAngle\\\":\\\"\\\",\\\"isMirrorH\\\":\\\"\\\",\\\"isMirrorV\\\":\\\"\\\",\\\"showMirrorDialog\\\":\\\"\\\",\\\"currentSplicedPieceNo\\\":\\\"\\\"}]}\"\n" +
                "}")*/

        val wsInputData = WSInputData(jsonString)

        return getWorkspaceService.updateWorkspaceDataFromApi(
            CLIENT_ID, SITE_ID, wsInputData,
            "Bearer " + AppState.getToken()!!
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
