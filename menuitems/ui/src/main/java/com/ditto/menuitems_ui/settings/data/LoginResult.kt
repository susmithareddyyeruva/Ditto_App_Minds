package com.ditto.menuitems_ui.settings.data

import com.google.gson.annotations.SerializedName

data class LoginResult(
    @SerializedName("auth_type")
    val authType: String,
    @SerializedName("creation_date")
    val creationDate: String,
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("customer_no")
    val customerNo: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_home")
    val phoneHome: String,
    @SerializedName("enabled")
    val enabled: Boolean,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("gender")
    val gender: Int,
    @SerializedName("last_login_time")
    val lastLoginTime: String,
    @SerializedName("last_modified")
    val lastModified: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("last_visit_time")
    val lastVisitTime: String,
    @SerializedName("login")
    val login: String,
    @SerializedName("previous_login_time")
    val previousLoginTime: String,
    @SerializedName("previous_visit_time")
    val previousVisitTime: String,
    @SerializedName("salutation")
    val salutation: String,
    @SerializedName("_type")
    val type: String,
    @SerializedName("_v")
    val v: String,

    @SerializedName("c_mirrorReminder")
    val cMirrorReminder: Boolean,
    @SerializedName("c_receiveEmail")
    val cReceiveEmail: Boolean,
    @SerializedName("c_spliceCutCompleteReminder")
    val cSpliceCutCompleteReminder: Boolean,
    @SerializedName("c_spliceMultiplePieceReminder")
    val cSpliceMultiplePieceReminder: Boolean,
    @SerializedName("c_spliceReminder")
    val cSpliceReminder: Boolean
)