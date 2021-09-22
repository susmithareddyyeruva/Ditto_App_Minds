package core.data.di

import core.data.SoftwareUpdateRepImpl
import core.data.SoftwareUpdateUsecaseImpl
import core.data.TokenRepositoryImpl
import core.data.TokenUseCaseImpl
import core.domain.GetTokenRepository
import core.domain.GetTokenUseCase
import core.domain.SoftwareUpdateRepository
import core.domain.SoftwareupdateUseCase
import dagger.Binds
import dagger.Module

@Module(includes = [VersioncaseModule::class])
interface SoftwareUpdateUseCaseModule {
    @Binds
    fun bindTokenRepository(versionRepositoryImpl: SoftwareUpdateRepImpl): SoftwareUpdateRepository
}

@Module
internal interface VersioncaseModule {
    @Binds
    fun bindGetLoginDbUseCase(
        getversionusecaseimpl: SoftwareUpdateUsecaseImpl
    ): SoftwareupdateUseCase

}

