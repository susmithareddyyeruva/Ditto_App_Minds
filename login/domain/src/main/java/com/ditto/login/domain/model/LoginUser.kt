package com.ditto.login.domain.model

data class LoginUser(
    val userName: String?,
    val _type: String?="",
    val auth_type: String?="",
    val customer_id: String?="",
    val customer_no: String?="",
    val email: String?="",
    val first_name: String?="",
    val gender: Int?=0,
    val last_login_time: String?="",
    val last_modified: String?="",
    val phone_home: String?="",
    val last_name: String?="",
    val last_visit_time: String?="",
    val login: String?="",
    val previous_login_time: String?="",
    val previous_visit_time: String?="",
    val salutation: String?="",
    val isLoggedIn: Boolean? = false,
    val dndOnboarding: Boolean? = false,
    val bleDialogVisible: Boolean? = false,
    val wifiDialogVisible: Boolean? = false,
    val cMirrorReminder: Boolean? = true,
    val cReceiveEmail:Boolean? = false,
    val cSpliceCutCompleteReminder: Boolean? = true,
    val cSpliceMultiplePieceReminder: Boolean? = true,
    val cSpliceReminder: Boolean? = true,
    val cCuttingReminder: Boolean? = true,
    val cInitialisationVector: String? = "",
    val cVectorKey: String? = "",
    val c_subscriptionPlanEndDate: String?="",
    val c_subscriptionValid: Boolean?=false


)