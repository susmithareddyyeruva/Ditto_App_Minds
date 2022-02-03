package com.ditto.menuitems.data

import com.ditto.menuitems.domain.AccountInfoRepository
import com.ditto.menuitems.domain.AccountInfoUsecase
import com.ditto.menuitems.domain.model.AccountInfoDomain
import io.reactivex.Single
import non_core.lib.Result
import javax.inject.Inject

class AccountInfoUsecaseImpl @Inject constructor(private val accountInfoRepository: AccountInfoRepository) :
    AccountInfoUsecase {
    override fun deleteAccount(custNO: String?): Single<Result<AccountInfoDomain>> {
        return accountInfoRepository.deleteAccount(custNO)
    }
}