package core.data.mapper

import core.data.model.TokenDetails
import core.data.model.TokenResult
import core.data.model.TokenResultDomain

fun TokenResult.toTokenDomain(): TokenResultDomain {

    return TokenResultDomain(
        response = this.response.toDomain(),
        errors = this.errors
    )
}

fun TokenDetails.toDomain(): TokenDetails {
    return TokenDetails(
        access_token = this.access_token ?: "",
        expires_in = this.expires_in ?: 0,
        sk = this.sk ?: 0
    )
}


