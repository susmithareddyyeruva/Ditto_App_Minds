package core.data.mapper

import core.data.model.SoftwareUpdateResult
import core.data.model.TokenDetails
import core.data.model.VersionResponse
import core.data.model.VersionResult

fun VersionResponse.toVersionDomain() : SoftwareUpdateResult {
    return  SoftwareUpdateResult(
        response = this.response.toDomain(),
        errors = this.errors
    )
}

fun VersionResult.toDomain(): VersionResult {
    return VersionResult(
        version = this.version,
        critical_version = this.critical_version,
        force_version = this.force_version,
        body = this.body,
        confirm = this.confirm,
        confirm_link = this.confirm_link,
        cancel = this.cancel,
        version_update = this.version_update,
        critical_update = this.critical_update,
        force_update = this.force_update
    )
}