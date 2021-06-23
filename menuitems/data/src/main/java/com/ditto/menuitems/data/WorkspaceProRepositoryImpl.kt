package com.ditto.menuitems.data

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.menuitems.data.api.WsSettingsService
import com.ditto.menuitems.data.error.WSProSettingFetchError
import com.ditto.menuitems.data.mapper.toDomain
import com.ditto.menuitems.domain.WorkspaceProRepository
import com.ditto.menuitems.domain.model.WSProSettingDomain
import com.ditto.menuitems.domain.model.WSSettingsInputData
import com.ditto.storage.data.database.UserDao
import core.CLIENT_ID
import core.appstate.AppState
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class WorkspaceProRepositoryImpl @Inject constructor(
    private val dbDataDao: @JvmSuppressWildcards UserDao,
    private val ws_settings: @JvmSuppressWildcards WsSettingsService,
    private val loggerFactory: LoggerFactory
) : WorkspaceProRepository {

    @Inject
    lateinit var context: Context
    val logger: Logger by lazy {
        loggerFactory.create(WorkspaceProRepositoryImpl::class.java.simpleName)
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

    override fun updateWSProSetting(
        id: Int,
        cMirrorReminder: Boolean,
        cCuttingReminder: Boolean,
        cSpliceReminder: Boolean,
        cSpliceMultiplePieceReminder: Boolean
    ): Single<Any> {
        return Single.fromCallable {
            dbDataDao.updateWSSettingUser(
                cMirrorReminder,
                cCuttingReminder,
                cSpliceReminder,
                cSpliceMultiplePieceReminder
            )
        }
    }

    override fun postSwitchData(data: WSSettingsInputData): Single<Result<WSProSettingDomain>> {

        return ws_settings.postSettingRequest( AppState.getCustID(),CLIENT_ID,
            data,
            "Bearer "+AppState.getToken()!!)
            .doOnSuccess {
                dbDataDao.updateWSSettingUser(data.c_mirrorReminder,data.c_cuttingReminder,
                data.c_spliceReminder,data.c_spliceMultiplePieceReminder)
                Log.d("result_success","doOnSuccess >>> ${it.toString()}")
            }.map {
                Result.withValue(it.toDomain())
            }.onErrorReturn {
                var errorMessage = "Error Fetching data"
                try {
                    logger.d("try block")
                    logger.d("${it.localizedMessage}")
                } catch (e: Exception) {
                    logger.d("Catch ${e.localizedMessage}")
                    errorMessage = e.message.toString()
                }
                Result.withError(
                    WSProSettingFetchError(errorMessage, it)
                )
            }
    }
}