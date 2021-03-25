package com.ditto.instructions.domain
/**
 * Created by Vishnu A V on  03/08/2020.
 * Repository class with functions
 */

import com.ditto.instructions.domain.model.InstructionsData
import io.reactivex.Single
import non_core.lib.Result

interface GetInstructionDataRepository {
    fun getinstructiondata(id: Int): Single<Result<InstructionsData>>
}