package com.ditto.workspace.data

import com.ditto.login.data.mapper.toUserDomain
import com.ditto.login.domain.LoginUser
import io.reactivex.Single
import non_core.lib.Result
import com.ditto.storage.data.database.PatternsDao
import com.ditto.storage.data.database.UserDao
import com.ditto.workspace.data.mapper.toDomain
import com.ditto.workspace.domain.WorkspaceRepository
import com.ditto.workspace.domain.model.PatternsData
import javax.inject.Inject

/**
 * Concrete class of WorkspaceRepository to expose Workspace Data from various sources (API, DB)
 */
class WorkspaceRepositoryImpl @Inject constructor(
    private val patternsDao: @JvmSuppressWildcards PatternsDao,
    private val dbDataDao: @JvmSuppressWildcards UserDao,

    ) : WorkspaceRepository {
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

    override fun deleteAndInsert(id:Int, patternsData: PatternsData): Single<Any> {
        return Single.fromCallable {
            patternsDao.deletePatternsData(id)
        }.flatMap { patternsDao.insertPatternsData(patternsData.toDomain()) }
    }

    override fun getUserData(): Single<Result<LoginUser>> {
        return Single.fromCallable{
            val data= dbDataDao.getUserData()
            if (data != null)
                Result.withValue(data.toUserDomain())
            else
                Result.withValue(LoginUser(""))
        }
    }
}
