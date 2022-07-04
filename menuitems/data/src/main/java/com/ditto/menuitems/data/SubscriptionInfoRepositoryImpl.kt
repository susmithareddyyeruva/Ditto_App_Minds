package com.ditto.menuitems.data

import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.domain.SubscriptionInfoRepository
import javax.inject.Inject

class SubscriptionInfoRepositoryImpl @Inject constructor(
    private val looggerFractory: LoggerFactory
) : SubscriptionInfoRepository {

    override fun getSubscriptionDetails() {
        TODO("Not yet implemented")
    }
}