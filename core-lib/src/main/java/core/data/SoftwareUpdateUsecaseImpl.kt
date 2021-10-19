package core.data

import core.data.model.SoftwareUpdateResult
import core.data.model.TokenResultDomain
import core.domain.GetTokenRepository
import core.domain.GetTokenUseCase
import core.domain.SoftwareUpdateRepository
import core.domain.SoftwareupdateUseCase
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class SoftwareUpdateUsecaseImpl @Inject constructor(
    private val versionRepository: SoftwareUpdateRepository
) : SoftwareupdateUseCase {

    override fun getVersion(): Single<Result<SoftwareUpdateResult>> {
        return versionRepository.getVersionRep()
    }
}