package com.ditto.login.data.mapper

import com.ditto.login.data.error.Arguments
import com.ditto.login.data.error.Fault
import com.ditto.login.data.model.CBody
import com.ditto.login.data.model.LandingContentResult
import com.ditto.login.data.model.LoginResult
import com.ditto.login.domain.error.ArgumentsDomain
import com.ditto.login.domain.error.FaultDomain
import com.ditto.login.domain.model.CBodyDomain
import com.ditto.login.domain.model.LandingContentDomain
import com.ditto.login.domain.model.LoginResultDomain
import com.ditto.login.domain.model.LoginUser
import com.ditto.storage.data.model.User

fun User.toUserDomain(): LoginUser {
    return LoginUser(
        userName = this.userName,
        isLoggedIn = this.isLoggedIn,
        dndOnboarding = this.dndOnboarding,
        bleDialogVisible = this.bleDialogVisible,
        wifiDialogVisible = this.wifiDialogVisible,
        cMirrorReminder = this.cMirrorReminder,
        cReceiveEmail = this.cReceiveEmail,
        cSpliceCutCompleteReminder = this.cSpliceCutCompleteReminder,
        cSpliceMultiplePieceReminder = this.cSpliceMultiplePieceReminder,
        cSpliceReminder = this.cSpliceReminder,
        cCuttingReminder = this.cCuttingReminder,
        cSaveCalibrationPhotos = this.cSaveCalibrationPhotos,
        cInitialisationVector = this.cInitialisationVector,
        cVectorKey = this.cVectorKey,
        cSubscriptionValid = this.cSubscriptionValid,
        cSubscriptionPlanEndDate = this.cSubscriptionPlanEndDate,
        cSubscriptionPlanStartDate = this.cSubscriptionPlanStartDate,
        cSubscriptionPlanPrice = this.cSubscriptionPlanPrice,
        cSubscriptionPlanId = this.cSubscriptionPlanId,
        cSubscriptionPlanName = this.cSubscriptionPlanName,
        cSubscriptionID = this.cSubscriptionID,
        cSubscriptionPlanCurrency = this.cSubscriptionPlanCurrency,
        cSubscriptionType = this.cSubscriptionType,
        cSubscriptionPlanBillingEndDate = this.cSubscriptionPlanBillingEndDate,
        cSubscriptionPlanBillingStartDate = this.cSubscriptionPlanBillingStartDate


    )
}

fun LoginUser.toDomain(): User {
    return User(
        userName = this.userName,
        isLoggedIn = this.isLoggedIn,
        dndOnboarding = this.dndOnboarding,
        bleDialogVisible = this.bleDialogVisible,
        wifiDialogVisible = this.wifiDialogVisible,
        cMirrorReminder = this.cMirrorReminder,
        cReceiveEmail = this.cReceiveEmail,
        cSpliceCutCompleteReminder = this.cSpliceCutCompleteReminder,
        cSpliceMultiplePieceReminder = this.cSpliceMultiplePieceReminder,
        cSpliceReminder = this.cSpliceReminder,
        cCuttingReminder = this.cCuttingReminder,
        cSaveCalibrationPhotos = this.cSaveCalibrationPhotos,
        cInitialisationVector = this.cInitialisationVector,
        cVectorKey = this.cVectorKey,

        cSubscriptionValid = this.cSubscriptionValid,
        cSubscriptionPlanEndDate = this.cSubscriptionPlanEndDate,
        cSubscriptionPlanStartDate = this.cSubscriptionPlanStartDate,
        cSubscriptionPlanPrice = this.cSubscriptionPlanPrice,
        cSubscriptionPlanId = this.cSubscriptionPlanId,
        cSubscriptionPlanName = this.cSubscriptionPlanName,
        cSubscriptionID = this.cSubscriptionID,
        cSubscriptionPlanCurrency = this.cSubscriptionPlanCurrency,
        cSubscriptionType = this.cSubscriptionType,
        cSubscriptionPlanBillingEndDate = this.cSubscriptionPlanBillingEndDate,
        cSubscriptionPlanBillingStartDate = this.cSubscriptionPlanBillingStartDate
    )


}

fun LoginResult.toUserDomain(): LoginResultDomain {
    return LoginResultDomain(
        _type = this.type,
        _v = this.v,
        authType = this.authType,
        creationDate = this.authType,
        customerId = this.customerId,
        customerNo = this.customerNo,
        email = this.email,
        enabled = this.enabled,
        firstName = this.firstName,
        gender = this.gender,
        phoneHome = this.phoneHome,
        lastLoginTime = this.lastLoginTime,
        lastModified = this.lastModified,
        lastName = this.lastName,
        lastVisitTime = this.lastVisitTime,
        login = this.login,
        previousLoginTime = this.previousLoginTime,
        previousVisitTime = this.previousVisitTime,
        salutation = this.salutation,
        faultDomain = this.fault?.toDomain(),
        cMirrorReminder = this.cMirrorReminder,
        cReceiveEmail = this.cReceiveEmail,
        cSpliceCutCompleteReminder = this.cSpliceCutCompleteReminder,
        cSpliceMultiplePieceReminder = this.cSpliceMultiplePieceReminder,
        cSpliceReminder = this.cSpliceReminder,
        cCuttingReminder = this.cCuttingReminder,
        cSaveCalibrationPhotos = this.cSaveCalibrationPhotos,
        cInterestArt = this.cInterestArt,
        cInterestBridalSpecialOccasionProjects = this.cInterestBridalSpecialOccasionProjects,
        cInterestClassroomCraftsDecor = this.cInterestClassroomCraftsDecor,
        cInterestFloral = this.cInterestFloral,
        cInterestFoodCrafts = this.cInterestFoodCrafts,
        cInterestHolidayPartyDecorating = this.cInterestHolidayPartyDecorating,
        cInterestHomeDecor = this.cInterestHomeDecor,
        cInterestJewelry = this.cInterestJewelry,
        cInterestKidsCrafts = this.cInterestKidsCrafts,
        cInterestKnittingCrochet = this.cInterestKnittingCrochet,
        cInterestPaperCrafts = this.cInterestPaperCrafts,
        cInterestQuiltingSewingFabric = this.cInterestQuiltingSewingFabric,
        cIsCostumeGuildEnrolled = this.cIsCostumeGuildEnrolled,
        cIsFourHEnrolled = this.cIsFourHEnrolled,
        cIsGirlScoutsEnrolled = this.cIsGirlScoutsEnrolled,
        cIsJoannPlusCustomer = this.cIsJoannPlusCustomer,
        cIsMilitaryEnrolled = this.cIsMilitaryEnrolled,
        cIsTaxExempt = this.cIsTaxExempt,
        cIsTeacherEnrolled = this.cIsTeacherEnrolled,
        cLearnBrowseSocialMedia = this.cLearnBrowseSocialMedia,
        cLearnLookOnline = this.cLearnLookOnline,
        cLearnTakeAClass = this.cLearnTakeAClass,
        cLearnVisitJoannStore = this.cLearnVisitJoannStore,
        cReceiveDirectMail = this.cReceiveDirectMail,
        cReceiveTextMessage = this.cReceiveTextMessage,
        cRegisteredWithNarvar = this.cRegisteredWithNarvar,
        cTaxExempt = this.cTaxExempt,
        cInitialisationVector = this.cInitialisationVector,
        cVectorKey = this.cVectorKey,

        cSubscriptionValid = this.cSubscriptionValid,
        cSubscriptionStatus = this.cSubscriptionStatus,
        cSubscriptionPlanEndDate = this.cSubscriptionPlanEndDate,
        cSubscriptionPlanStartDate = this.cSubscriptionPlanStartDate,
        cSubscriptionPlanPrice = this.cSubscriptionPlanPrice,
        cSubscriptionPlanId = this.cSubscriptionPlanId,
        cSubscriptionPlanName = this.cSubscriptionPlanName,
        cSubscriptionID = this.cSubscriptionID,
        cSubscriptionPlanCurrency = this.cSubscriptionPlanCurrency,
        cSubscriptionType = this.cSubscriptionType,
        cSubscriptionPlanBillingEndDate = this.cSubscriptionPlanBillingEndDate,
        cSubscriptionPlanBillingStartDate = this.cSubscriptionPlanBillingStartDate,
        cEncryptionkey = this.c_encryptionKey


    )

}

fun Fault.toDomain(): FaultDomain {
    return FaultDomain(
        arguments = this.arguments.toDomain(),
        message = this.message,
        type = this.type
    )
}

fun Arguments.toDomain(): ArgumentsDomain {
    return ArgumentsDomain(
        credentialType = this.credentialType
    )
}
fun LandingContentResult.toDomain():LandingContentDomain{
    return LandingContentDomain(_type = this.type,_v = this.v,id = this.id,c_body = this.cBody.toDomain(),
    name = this.name)
}
fun CBody.toDomain():CBodyDomain{
    return CBodyDomain(customerCareEmail=this.customerCareEmail,
    customerCareePhone = this.customerCareePhone,
    customerCareeTiming = this.customerCareeTiming,
    videoUrl = this.videoUrl,
    imageUrl = this.imageUrl)
}

