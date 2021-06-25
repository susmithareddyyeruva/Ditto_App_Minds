package core.data

import android.content.Context
import core.TOKEN_BODY
import core.data.mapper.toTokenDomain
import core.data.model.TokenFetchError
import core.di.ApiService
import core.domain.GetTokenRepository
import core.network.NetworkUtility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val apiService: ApiService,

) : GetTokenRepository {
    @Inject
    lateinit var context: Context
    override fun getTokenRep(): Single<Result<TokenResultDomain>> {
        if (!NetworkUtility.isNetworkAvailable(context)) {
            return Single.just(Result.OnError(NoNetworkError()))
        }
        val tokenreq:HashMap<String,String> = HashMap<String,String>()
        tokenreq.put("grant_type", TOKEN_BODY)
        return apiService.refreshToken(tokenreq)
                .doOnSuccess {

                }
               .map {

                Result.withValue(it.toTokenDomain())

            } .onErrorReturn {
                  val error = it.localizedMessage
                Result.withError(
                    TokenFetchError(error, it)
                )
            }

    }
}