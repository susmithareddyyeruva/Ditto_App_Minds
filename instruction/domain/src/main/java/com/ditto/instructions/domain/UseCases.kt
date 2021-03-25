package com.ditto.instructions.domain

/**
 * Created by Vishnu A V on  03/08/2020.
 * Usecase class invoking from view model
 */
import non_core.lib.Result
import com.ditto.instructions.domain.model.InstructionsData
import io.reactivex.Single

interface GetInstructionDataUsecase{
    fun invoke(id: Int) : Single<Result<InstructionsData>>
}