package com.ditto.workspace.domain

import io.reactivex.Single
import non_core.lib.Result
import com.ditto.workspace.domain.model.PatternsData

/**
 * Repository interface defining methods to be used by upper (UI/UseCase) layer
 */
interface WorkspaceRepository {
    fun getWorkspaceData(): Single<Result<List<PatternsData>>>
    fun insertData(patternsData: PatternsData): Single<Any>
    fun deleteAndInsert(id:Int, patternsData: PatternsData): Single<Any>
}