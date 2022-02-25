package com.ditto.menuitems.data.mapper

import com.ditto.menuitems.data.model.DeleteAccountInfoResult
import com.ditto.menuitems.domain.model.AboutAppDomain
import com.ditto.menuitems.domain.model.AboutAppResponseData
import com.ditto.menuitems.domain.model.AccountInfoDomain

fun DeleteAccountInfoResult.toDomain(): AccountInfoDomain {
    return AccountInfoDomain(
        v=this.v
    )
}