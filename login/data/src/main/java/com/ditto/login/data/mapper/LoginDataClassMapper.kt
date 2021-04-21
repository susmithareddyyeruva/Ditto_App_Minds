package com.ditto.login.data.mapper

import com.ditto.login.data.model.LoginResult
import com.ditto.login.domain.LoginResultDomain

fun LoginResult.toDomain(): LoginResultDomain {
    return LoginResultDomain(
            _type = this.type,
            _v = this.v,
            auth_type = this.authType,
            creation_date = this.authType,
            customer_id = this.customerId,
            customer_no=this.customerNo,
            email=this.email,
            enabled=this.enabled,
            first_name= this.firstName,
            gender= this.gender,
            last_login_time= this.lastLoginTime,
            last_modified= this.lastModified,
            last_name= this.lastName,
            last_visit_time= this.lastVisitTime,
            login = this.login,
            previous_login_time = this.previousLoginTime,
            previous_visit_time = this.previousVisitTime,
            salutation = this.salutation
    )
}
fun LoginResult.toUserDomain(): LoginResultDomain {
    return LoginResultDomain(
        _type = this.type,
        _v = this.v,
        auth_type = this.authType,
        creation_date = this.authType,
        customer_id = this.customerId,
        customer_no=this.customerNo,
        email=this.email,
        enabled=this.enabled,
        first_name= this.firstName,
        gender= this.gender,
        last_login_time= this.lastLoginTime,
        last_modified= this.lastModified,
        last_name= this.lastName,
        last_visit_time= this.lastVisitTime,
        login = this.login,
        previous_login_time = this.previousLoginTime,
        previous_visit_time = this.previousVisitTime,
        salutation = this.salutation

    )
}
