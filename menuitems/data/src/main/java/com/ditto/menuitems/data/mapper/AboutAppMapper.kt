package com.ditto.menuitems.data.mapper

import com.ditto.menuitems.domain.model.AboutAppDomain
import com.ditto.menuitems.domain.model.AboutAppResponseData

fun AboutAppResponseData.toDomain():AboutAppDomain{
    return AboutAppDomain(
        name = this.name,
        id = this.id,
        c_body = this.c_body,
    )
}