package com.ditto.login.domain

data class LoginUser(
    val userName: String?,
    val isLoggedIn: Boolean? = false,
    val dndOnboarding: Boolean? = false,
    val bleDialogVisible: Boolean? = false,
    val wifiDialogVisible: Boolean? = false

)