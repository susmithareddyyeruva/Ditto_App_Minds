package com.ditto.menuitems.data.di

import com.ditto.menuitems.data.AccountInfoRepositoryImpl
import com.ditto.menuitems.data.AccountInfoUsecaseImpl
import com.ditto.menuitems.domain.AccountInfoRepository
import com.ditto.menuitems.domain.AccountInfoUsecase
import dagger.Binds
import dagger.Module

@Module(includes = [AccountInfoModule::class])
interface AccountInfoDataModule {
    @Binds
    fun bindAccountInfoRepository(accountInfoRepositoryImpl: AccountInfoRepositoryImpl): AccountInfoRepository
}

@Module
internal interface AccountInfoModule {
    @Binds
    fun bindAccountInfoUsecase(
        accountInfoUsecaseImpl: AccountInfoUsecaseImpl
    ): AccountInfoUsecase
}