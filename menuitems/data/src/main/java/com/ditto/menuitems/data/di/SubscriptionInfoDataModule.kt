package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.AccountInfoUsecaseImpl
import com.ditto.menuitems.data.SubscriptionInfoRepositoryImpl
import com.ditto.menuitems.data.SubscriptionInfoUsecaseImpl
import com.ditto.menuitems.domain.SubscriptionInfoRepository
import com.ditto.menuitems.domain.SubscriptionInfoUsecase
import dagger.Binds
import dagger.Module

@Module(includes = [SubscriptionInfoModule::class])
interface SubscriptionInfoDataModule{
    @Binds
    fun SubscriptionInfoRepository (subscriptionInfoRepository: SubscriptionInfoRepositoryImpl):SubscriptionInfoRepository
}


@Module
internal interface SubscriptionInfoModule {
    @Binds fun bindSubscriptionInfoUsecase(subscriptionDetaUsecaseImpl: SubscriptionInfoUsecaseImpl):SubscriptionInfoUsecase
}