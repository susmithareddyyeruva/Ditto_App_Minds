package core.domain

import core.data.model.SoftwareUpdateResult
import core.data.model.TokenResultDomain
import io.reactivex.Single
import non_core.lib.Result


interface SoftwareUpdateRepository {
    fun getVersionRep() : Single<Result<SoftwareUpdateResult>>
}