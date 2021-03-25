package com.ditto.instructions.data

/**
 * Created by Vishnu A V on  03/08/2020.
 * Class representing functions from usecase class
 */
import com.ditto.instructions.domain.GetInstructionDataRepository
import com.ditto.instructions.domain.GetInstructionDataUsecase
import com.ditto.instructions.domain.model.InstructionsData
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class GetInstructionDataImpl @Inject constructor(
    private val instructionRepository: GetInstructionDataRepository
) : GetInstructionDataUsecase {
    override fun invoke(id: Int): Single<Result<InstructionsData>> {
        return instructionRepository.getinstructiondata(id)
    }
}