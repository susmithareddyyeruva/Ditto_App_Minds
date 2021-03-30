package com.ditto.login.data.mapper

import com.ditto.login.domain.LoginUser
import com.ditto.storage.data.model.User

fun User.toUserDomain(): LoginUser {
    return LoginUser(
        userName = this.userName,
        isLoggedIn = this.isLoggedIn,
        dndOnboarding = this.dndOnboarding,
        bleDialogVisible = this.bleDialogVisible,
        wifiDialogVisible = this.wifiDialogVisible

    )
}

fun LoginUser.toDomain(): User {
    return User(
        userName = this.userName,
        isLoggedIn = this.isLoggedIn,
        dndOnboarding = this.dndOnboarding,
        bleDialogVisible = this.bleDialogVisible,
        wifiDialogVisible = this.wifiDialogVisible
    )
}
