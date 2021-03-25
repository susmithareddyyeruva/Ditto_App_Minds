package com.ditto.howto.di

import com.ditto.howto.GetHowToDataImpl
import com.ditto.howto.GetHowToDataRepository
import com.ditto.howto.GetHowToDataRepositoryImpl
import com.ditto.howto.GetHowToDataUsecase
import dagger.Binds
import dagger.Module

/**
 * Created by Sesha on  15/08/2020.
 * Dagger module to provide Repository and usecase  functionality.
 */

@Module(includes = [HowToUsecaseModule::class])
interface HowToModule {
    @Binds
    fun bindHowToRepository(howtoRepositoryimpl: GetHowToDataRepositoryImpl): GetHowToDataRepository
}

@Module
internal interface HowToUsecaseModule {

@Binds
fun bindHowToDataUseCase(
    gethowtodataimpl: GetHowToDataImpl
) : GetHowToDataUsecase

}