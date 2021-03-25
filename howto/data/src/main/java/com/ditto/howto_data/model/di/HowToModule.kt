package com.ditto.howto_data.model.di

import com.ditto.howto_data.model.GetHowToDataRepositoryImpl
import dagger.Binds
import dagger.Module
import com.ditto.howto_data.model.GetHowToDataImpl
import com.ditto.howto_domain.GetHowToDataRepository
import com.ditto.howto_domain.GetHowToDataUsecase

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