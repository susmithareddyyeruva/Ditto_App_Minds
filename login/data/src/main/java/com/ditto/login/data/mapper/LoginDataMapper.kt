package com.ditto.login.data.mapper

import com.ditto.login.domain.LoginUser
import com.ditto.storage.data.model.User

fun User.toUserDomain(): LoginUser {
    return LoginUser(
        userName = this.userName,
        isLoggedIn = this.isLoggedIn,
        dndOnboarding = this.dndOnboarding,
        bleDialogVisible = this.bleDialogVisible,
        wifiDialogVisible = this.wifiDialogVisible,
        cMirrorReminder = this.cMirrorReminder,
        cReceiveEmail = this.cReceiveEmail,
        cSpliceCutCompleteReminder = this.cSpliceCutCompleteReminder,
        cSpliceMultiplePieceReminder = this.cSpliceMultiplePieceReminder,
        cSpliceReminder = this.cSpliceReminder,
        cCuttingReminder = this.cCuttingReminder,
        cInitialisationVector = this.cInitialisationVector,
        cVectorKey = this.cVectorKey


    )
}

fun LoginUser.toDomain(): User {
    return User(
        userName = this.userName,
        isLoggedIn = this.isLoggedIn,
        dndOnboarding = this.dndOnboarding,
        bleDialogVisible = this.bleDialogVisible,
        wifiDialogVisible = this.wifiDialogVisible,
        cMirrorReminder = this.cMirrorReminder,
        cReceiveEmail = this.cReceiveEmail,
        cSpliceCutCompleteReminder = this.cSpliceCutCompleteReminder,
        cSpliceMultiplePieceReminder = this.cSpliceMultiplePieceReminder,
        cSpliceReminder = this.cSpliceReminder,
        cCuttingReminder = this.cCuttingReminder,
        cInitialisationVector = this.cInitialisationVector,
        cVectorKey = this.cVectorKey
    )
}
