package com.ditto.onboarding.data.mapper

import com.ditto.onboarding.domain.model.OnboardingData
import com.ditto.storage.data.model.OnBoarding

internal fun List<OnBoarding>.toDomain():List<OnboardingData> {
   return this.map {
       OnboardingData(
           id = it.id,
           title = it.title,
           description = it.description,
           image = it.instructions[0].imagePath
       )
   }
}






