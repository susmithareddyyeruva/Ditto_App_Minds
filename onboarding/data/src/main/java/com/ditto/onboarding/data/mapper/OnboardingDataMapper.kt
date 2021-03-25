package com.ditto.onboarding.data.mapper

import com.ditto.storage.data.model.OnBoarding
import com.ditto.storage.data.model.User
import com.ditto.onboarding.domain.model.OnboardingData

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

//internal fun User.toDomain(): LoginUser {
//    return LoginUser(
//        userName = this.userName,
//        isLoggedIn = this.isLoggedIn,
//        dndOnboarding = this.dndOnboarding
//    )
//}





