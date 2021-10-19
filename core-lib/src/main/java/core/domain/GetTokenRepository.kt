package core.domain

import core.data.model.TokenResultDomain
import io.reactivex.Single
import non_core.lib.Result

interface GetTokenRepository {
    fun getTokenRep() : Single<Result<TokenResultDomain>>
}