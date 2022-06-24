package com.ditto.menuitems.data

import com.ditto.menuitems.domain.SubscriptionInfoRepository
import com.ditto.menuitems.domain.SubscriptionInfoUsecase
import javax.inject.Inject

class SubscriptionInfoUsecaseImpl @Inject constructor(private val subscriptionInfoRepository: SubscriptionInfoRepository) :
    SubscriptionInfoUsecase {

}