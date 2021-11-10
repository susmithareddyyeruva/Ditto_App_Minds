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
import core.CONNECTION_EXCEPTION
import core.ERROR_FETCH
import core.UNKNOWN_HOST_EXCEPTION
import core.appstate.AppState
import io.reactivex.Single
import non_core.lib.Result
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException
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

        return ws_settings.postSettingRequest(
            AppState.getCustID(), CLIENT_ID,
            data,
            "Bearer " + AppState.getToken()!!
        )
            .doOnSuccess {
                dbDataDao.updateWSSettingUser(
                    data.c_mirrorReminder, data.c_cuttingReminder,
                    data.c_spliceReminder, data.c_spliceMultiplePieceReminder
                )
                Log.d("result_success", "doOnSuccess >>> ${it.toString()}")
            }.map {
                Result.withValue(it.toDomain())
            }.onErrorReturn {
                var errorMessage = "Error Fetching data"
                if (it is HttpException) {
                    val httpException = it as HttpException
                    when (httpException.code()) {
                        400 -> {
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
                    WSProSettingFetchError(errorMessage, it)
                )
            }

    }

}