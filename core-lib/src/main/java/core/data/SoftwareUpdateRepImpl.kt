package core.data

import android.content.Context
import core.data.mapper.toVersionDomain
import core.data.model.SoftwareUpdateResult
import core.data.model.TokenFetchError
import core.di.ApiService
import core.domain.SoftwareUpdateRepository
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

class SoftwareUpdateRepImpl @Inject constructor(
    private val apiService: ApiService,

    ) : SoftwareUpdateRepository {
    @Inject
    lateinit var context: Context
    override fun getVersionRep(): Single<Result<SoftwareUpdateResult>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        return apiService.checkVersion()
            .doOnSuccess {

            }
            .map {

                Result.withValue(it.toVersionDomain())

            }.onErrorReturn {
                val error = it.localizedMessage
                Result.withError(
                    TokenFetchError(error, it)
                )
            }
    }
}