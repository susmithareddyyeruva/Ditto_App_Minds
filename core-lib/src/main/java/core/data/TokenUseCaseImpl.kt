package core.data

import core.data.model.TokenResultDomain
import core.domain.GetTokenRepository
import core.domain.GetTokenUseCase
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class TokenUseCaseImpl @Inject constructor(
    private val tokenRepository: GetTokenRepository
) : GetTokenUseCase{

    override fun getToken(): Single<Result<TokenResultDomain>> {
        return tokenRepository.getTokenRep()
    }
}