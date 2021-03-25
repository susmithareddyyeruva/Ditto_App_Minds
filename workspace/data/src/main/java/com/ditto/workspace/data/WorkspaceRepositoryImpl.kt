package com.ditto.workspace.data

import io.reactivex.Single
import non_core.lib.Result
import com.ditto.storage.data.database.PatternsDao
import com.ditto.workspace.data.mapper.toDomain
import com.ditto.workspace.domain.WorkspaceRepository
import com.ditto.workspace.domain.model.PatternsData
import javax.inject.Inject

/**
 * Concrete class of WorkspaceRepository to expose Workspace Data from various sources (API, DB)
 */
class WorkspaceRepositoryImpl @Inject constructor(
    private val patternsDao: @JvmSuppressWildcards PatternsDao
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
}
