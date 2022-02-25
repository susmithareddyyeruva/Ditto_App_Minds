package core.domain

import core.data.model.SoftwareUpdateResult
import io.reactivex.Single
import non_core.lib.Result

interface SoftwareupdateUseCase {
    fun getVersion() : Single<Result<SoftwareUpdateResult>>
}