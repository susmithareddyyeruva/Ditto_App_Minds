package core.data.mapper

import core.data.model.SoftwareUpdateResult
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
        criticalVersion = this.criticalVersion,
        forceVersion = this.forceVersion,
        title = this.title,
        body = this.body,
        confirm = this.confirm,
        confirmLink = this.confirmLink,
        cancel = this.cancel,
        versionUpdate = this.versionUpdate,
        criticalUpdate = this.criticalUpdate,
        forceUpdate = this.forceUpdate
    )
}