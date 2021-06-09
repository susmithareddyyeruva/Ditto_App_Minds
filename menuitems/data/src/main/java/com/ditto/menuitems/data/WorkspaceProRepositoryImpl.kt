package com.ditto.menuitems.data

import android.content.Context
import android.util.Log
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.api.LoginRepositoryImpl
import com.ditto.login.data.error.LoginFetchError
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginUser
import com.ditto.menuitems.data.api.WsSettingsService
import com.ditto.menuitems.domain.WorkspaceProRepository
import com.ditto.menuitems.domain.model.LoginResult
import com.ditto.menuitems.domain.model.WSSettingsInputData
import com.ditto.menuitems.domain.model.WsSettingsPostError
import com.ditto.storage.data.database.UserDao
import core.CLIENT_ID
import core.appstate.AppState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
        loggerFactory.create(LoginRepositoryImpl::class.java.simpleName)
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

    override fun postSwitchData(data: WSSettingsInputData): Single<Result<LoginResult>> {

        return ws_settings.postSettingRequest( AppState.getCustID(),CLIENT_ID,
            data,
            "Bearer "+AppState.getToken()!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
                /*dbDataDao.updateWSSettingUser(data.c_mirrorReminder,data.c_spliceCutCompleteReminder,
                data.c_spliceReminder,data.c_spliceMultiplePieceReminder)*/
                Result.withValue(it)
            }.onErrorReturn {
                Result.withError(
                    WsSettingsPostError(it.cause!!.localizedMessage, it)
                )
            }
    }
}