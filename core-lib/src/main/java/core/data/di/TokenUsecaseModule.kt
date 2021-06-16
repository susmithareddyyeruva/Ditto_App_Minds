package core.data.di

import core.data.TokenRepositoryImpl
import core.data.TokenUseCaseImpl
import core.domain.GetTokenRepository
import core.domain.GetTokenUseCase
import dagger.Binds
import dagger.Module



@Module(includes = [TokencaseModule::class])
interface TokenUsecaseModule {
    @Binds
    fun bindTokenRepository(tokenRepositoryImpl: TokenRepositoryImpl): GetTokenRepository
}

@Module
internal interface TokencaseModule {
    @Binds
    fun bindGetLoginDbUseCase(
        gettokenusecaseimpl: TokenUseCaseImpl
    ): GetTokenUseCase

}