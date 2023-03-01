package com.ditto.menuitems.domain.model

data class WSSettingsInputData(
    var c_mirrorReminder: Boolean,
    var c_cuttingReminder: Boolean,
    var c_spliceReminder: Boolean,
    var c_spliceMultiplePieceReminder: Boolean,
    var c_saveCalibrationPhotos: Boolean,
    var c_receiveEmail: Boolean
)