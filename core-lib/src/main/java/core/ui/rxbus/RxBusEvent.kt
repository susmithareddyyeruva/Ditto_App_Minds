package core.ui.rxbus

import core.data.model.SoftwareUpdateResult


class RxBusEvent {

    data class CheckVersion(val isCheckVersion: Boolean)
    data class VersionReceived(val versionReceived: SoftwareUpdateResult)
    data class VersionErrorReceived(val versionerrorReceived: String)

}