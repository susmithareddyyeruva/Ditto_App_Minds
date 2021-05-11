package com.ditto.onboarding.data.mapper

import com.ditto.onboarding.data.model.*
import com.ditto.onboarding.domain.model.*
import com.ditto.storage.data.model.Instructions
import com.ditto.storage.data.model.OnBoarding

internal fun List<OnBoarding>.toDomain(): List<OnboardingData> {
    return this.map {
        OnboardingData(
            id = it.id,
            title = it.title,
            description = it.description,
            image = it.imagepath
        )
    }
}

fun OnBoardingResult.toDomain(): OnBoardingResultDomain {
    return OnBoardingResultDomain(
        _type = this.type,
        _v = this.version,
        c_body = this.cBody.toDomain(),
        id = this.id,
        name = this.name,


        )
}


fun CBody.toDomain(): CBodyDomain {
    return CBodyDomain(
        onboarding = this.onboarding.map { it.toDomain() }
    )
}

fun Onboarding.toDomain(): OnboardingDomain {
    return OnboardingDomain(
        description = this.description,
        id = this.id,
        imagePath = this.imagePath,
        title = this.title,
        instructions = this.instructions?.map { it.toDomain() })
}

fun List<Onboarding>.toStorage(): List<OnBoarding> {
    return this.map {
        OnBoarding(
            id = it.id ?: 0,
            title = it.title ?: "",
            description = it.description ?: "",
            imagepath = it.imagePath ?: "",
            instructions = it.instructions?.toStorageModel()?: emptyList()
        )
    }
}

fun List<Instruction>.toStorageModel(): List<Instructions> {
    return this.map {
        Instructions(
            id = it.id ?: 0,
            title = it.title ?: "",
            description = it.description ?: "",
            imagePath = it.imagePath ?: "",
            instructions = it.instructionsOnboarding?.toStorage()?: emptyList()
        )
    }

}

fun List<InstructionX>.toStorageClass(): List<OnBoarding> {
    return this.map {
        OnBoarding(
            id = it.id ?: 0,
            title = it.title ?: "",
            description = it.description ?: "",
            imagepath = it.imagePath ?: ""
        )
    }
}

fun Instruction.toDomain(): InstructionDomain {
    return InstructionDomain(description = this.description,
        id = this.id,
        imagePath = this.imagePath,
        title = this.title,
        videoPath = this.videoPath,
        instructions = this.instructionsOnboarding?.map { it.toDomain() })
}


/*fun InstructionX.toDomain(): InstructionXDomain {
    return InstructionXDomain(
        description = this.description,
        id = this.id,
        videoPath = this.videoPath,
        imagePath = this.imagePath,
        title = this.title
    )
}*/






