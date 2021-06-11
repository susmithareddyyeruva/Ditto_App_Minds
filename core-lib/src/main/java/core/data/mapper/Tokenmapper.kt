package core.data.mapper

import core.data.TokenResultDomain
import core.data.model.TokenResult

fun TokenResult.toTokenDomain() : TokenResultDomain {

    return TokenResultDomain(
        access_token = this.access_token,
        expires_in = this.expires_in,
        token_type = this.token_type,
        error = this.error,
        error_description = this.error_description
    )
}