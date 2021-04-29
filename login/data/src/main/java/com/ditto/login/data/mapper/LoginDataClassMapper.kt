package com.ditto.login.data.mapper

import com.ditto.login.data.error.Arguments
import com.ditto.login.data.error.Fault
import com.ditto.login.data.model.LoginResult
import com.ditto.login.domain.LoginResultDomain
import com.ditto.login.domain.error.ArgumentsDomain
import com.ditto.login.domain.error.FaultDomain

fun LoginResult.toUserDomain(): LoginResultDomain {
    return LoginResultDomain(
        _type = this.type,
        _v = this.v,
        auth_type = this.authType,
        creation_date = this.authType,
        customer_id = this.customerId,
        customer_no = this.customerNo,
        email = this.email,
        enabled = this.enabled,
        first_name = this.firstName,
        gender = this.gender,
        phone_home = this.phoneHome,
        last_login_time = this.lastLoginTime,
        last_modified = this.lastModified,
        last_name = this.lastName,
        last_visit_time = this.lastVisitTime,
        login = this.login,
        previous_login_time = this.previousLoginTime,
        previous_visit_time = this.previousVisitTime,
        salutation = this.salutation,
        faultDomain = this.fault?.toDomain()

    )

}

fun Fault.toDomain(): FaultDomain {
    return FaultDomain(
        arguments = this.arguments.toDomain(),
        message = this.message,
        type = this.type
    )
}

fun Arguments.toDomain(): ArgumentsDomain {
    return ArgumentsDomain(
        credentialType = this.credentialType
    )
}