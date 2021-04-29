package com.ditto.login.domain

import com.ditto.login.domain.error.FaultDomain

data class LoginResultDomain(
    val _type: String?,
    val _v: String?,
    val auth_type: String?,
    val creation_date: String?,
    val customer_id: String?,
    val customer_no: String?,
    val email: String?,
    val enabled: Boolean?,
    val first_name: String?,
    val gender: Int?,
    val last_login_time: String?,
    val last_modified: String?,
    val phone_home: String?,
    val last_name: String?,
    val last_visit_time: String,
    val login: String?,
    val previous_login_time: String?,
    val previous_visit_time: String?,
    val salutation: String?,
    val faultDomain: FaultDomain?
)