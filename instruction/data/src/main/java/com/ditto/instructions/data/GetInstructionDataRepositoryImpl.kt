package com.ditto.instructions.data

/**
 * Created by Vishnu A V on  03/08/2020.
 * Repository class with DB calls
 */
import com.ditto.instructions.domain.GetInstructionDataRepository
import com.ditto.instructions.domain.model.InstructionsData
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.FeatureError
import com.ditto.storage.data.database.OnBoardingDao
import com.ditto.instructions.data.mapper.toDomain
import javax.inject.Inject

class GetInstructionDataRepositoryImpl @Inject constructor(

    private val instructionsDataDao: @JvmSuppressWildcards OnBoardingDao

) : GetInstructionDataRepository {

    override fun getinstructiondata(id: Int): Single<Result<InstructionsData>> {
        return Single.fromCallable {

            val data = instructionsDataDao.getOnboardingDataByID(id)
            Result.withValue(data.toDomain())
        }.onErrorReturn {
            Result.withError(
                DataFetchError(
                    "Error fetching data",
                    it
                )
            )
        }
    }
}

open class DataFetchError(
    message: String = "Error Fetching data",
    throwable: Throwable? = null
) : FeatureError(message, throwable)
