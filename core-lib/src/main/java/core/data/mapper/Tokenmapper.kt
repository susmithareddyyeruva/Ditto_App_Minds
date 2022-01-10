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
        accessToken = this.accessToken ?: "",
        expiresIn = this.expiresIn ?: 0,
        sk = this.sk ?: 0
    )
}


