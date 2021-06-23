package com.ditto.login.data.di


import com.ditto.login.data.api.GetLoginDbUseCaseImpl
import com.ditto.login.data.api.LoginRepositoryImpl
import com.ditto.login.domain.LoginRepository
import com.ditto.login.domain.model.GetLoginDbUseCase
import dagger.Binds
import dagger.Module

/**
 * Dagger module to provide injections for UseCase implementation
 */
@Module(includes = [UseCaseModule::class])
interface LoginDbModule {
    @Binds
    fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository
}

@Module
internal interface UseCaseModule {
    @Binds
    fun bindGetLoginDbUseCase(
        getDbUseCaseImpl: GetLoginDbUseCaseImpl
    ): GetLoginDbUseCase

}