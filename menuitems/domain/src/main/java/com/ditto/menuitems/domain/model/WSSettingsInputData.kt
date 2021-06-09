package com.ditto.menuitems.domain.model

data class WSSettingsInputData(
    var c_mirrorReminder: Boolean,
    var c_spliceCutCompleteReminder: Boolean,
    var c_spliceReminder: Boolean,
    var c_spliceMultiplePieceReminder: Boolean,
    var zoomNotification: Boolean
)