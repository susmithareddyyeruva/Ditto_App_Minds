package com.ditto.instructions.data.di

/**
 * Created by Vishnu A V on  03/08/2020.
 * Dagger module to provide Repository and usecase  functionality.
 */
import dagger.Binds
import dagger.Module
import com.ditto.instructions.data.GetInstructionDataImpl
import com.ditto.instructions.domain.GetInstructionDataRepository
import com.ditto.instructions.domain.GetInstructionDataUsecase
import com.ditto.instructions.data.GetInstructionDataRepositoryImpl


@Module(includes = [InstructionUsecaseModule::class])
interface InstructionModule {
    @Binds
    fun bindInstructionRepository(instructionRepositoryimpl: GetInstructionDataRepositoryImpl): GetInstructionDataRepository
}

@Module
internal interface InstructionUsecaseModule {

    @Binds
    fun bindInstructionDataUseCase(
        getinstructiondataimpl: GetInstructionDataImpl
    ): GetInstructionDataUsecase

}