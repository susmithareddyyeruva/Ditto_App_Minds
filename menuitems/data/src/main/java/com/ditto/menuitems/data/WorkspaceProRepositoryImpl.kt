package com.ditto.menuitems.data

import android.content.Context
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.api.LoginRepositoryImpl
import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginUser
import com.ditto.menuitems.domain.WorkspaceProRepository
import com.ditto.storage.data.database.UserDao
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class WorkspaceProRepositoryImpl @Inject constructor(
    private val dbDataDao: @JvmSuppressWildcards UserDao,
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
        return Single.fromCallable{
            dbDataDao.updateWSSettingUser(id, cMirrorReminder, cCuttingReminder, cSpliceReminder, cSpliceMultiplePieceReminder)
        }
    }
}