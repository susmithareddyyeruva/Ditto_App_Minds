package core.ui.rxbus

import core.data.model.SoftwareUpdateResult


class RxBusEvent {

    data class checkVersion(val isCheckVersion: Boolean)
    data class versionReceived(val versionReceived: SoftwareUpdateResult)
    data class versionErrorReceived(val versionerrorReceived: String)

}