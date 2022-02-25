package com.ditto.menuitems.domain

import com.ditto.menuitems.domain.model.AccountInfoDomain
import io.reactivex.Single
import non_core.lib.Result

interface AccountInfoRepository {
    fun deleteAccount(custNO: String?): Single<Result<AccountInfoDomain>>
}