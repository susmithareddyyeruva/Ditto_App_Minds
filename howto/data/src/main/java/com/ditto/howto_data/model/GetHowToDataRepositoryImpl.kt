package com.ditto.howto_data.model

import com.ditto.howto_data.model.mapper.toDomain
import com.ditto.howto_domain.GetHowToDataRepository
import com.ditto.howto_domain.model.HowToData
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.FeatureError
import com.ditto.storage.data.database.OnBoardingDao
import javax.inject.Inject

/**
 * Created by Sesha on  15/08/2020.
 * Repository class with DB calls
 */
class GetHowToDataRepositoryImpl @Inject constructor(

    private val howtoDataDao: @JvmSuppressWildcards OnBoardingDao

): GetHowToDataRepository {

     override fun gethowtodata(id: Int): Single<Result<HowToData>> {
         return Single.fromCallable {

             val data = howtoDataDao.getOnboardingDataByID(id)
             Result.withValue(data.toDomain())
         }.onErrorReturn {Result.withError(
             DataFetchError("Error fetching data", it)
         )  }
     }
}
open class DataFetchError(
    message: String = "Data Fetching error",
    throwable: Throwable? = null
) : FeatureError(message, throwable)
