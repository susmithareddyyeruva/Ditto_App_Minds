package core.domain
import core.data.model.TokenResultDomain
import io.reactivex.Single
import non_core.lib.Result

interface GetTokenUseCase {
    fun getToken() : Single<Result<TokenResultDomain>>
}