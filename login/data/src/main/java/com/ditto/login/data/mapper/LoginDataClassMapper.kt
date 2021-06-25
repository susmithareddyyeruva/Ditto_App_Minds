package com.ditto.login.data.mapper

import com.ditto.login.data.error.Arguments
import com.ditto.login.data.error.Fault
import com.ditto.login.data.model.LoginResult
import com.ditto.login.domain.LoginResultDomain
import com.ditto.login.domain.error.ArgumentsDomain
import com.ditto.login.domain.error.FaultDomain

fun LoginResult.toUserDomain(): LoginResultDomain {
    return LoginResultDomain(
        _type = this.type,
        _v = this.v,
        auth_type = this.authType,
        creation_date = this.authType,
        customer_id = this.customerId,
        customer_no = this.customerNo,
        email = this.email,
        enabled = this.enabled,
        first_name = this.firstName,
        gender = this.gender,
        phone_home = this.phoneHome,
        last_login_time = this.lastLoginTime,
        last_modified = this.lastModified,
        last_name = this.lastName,
        last_visit_time = this.lastVisitTime,
        login = this.login,
        previous_login_time = this.previousLoginTime,
        previous_visit_time = this.previousVisitTime,
        salutation = this.salutation,
        faultDomain = this.fault?.toDomain(),
        cMirrorReminder = this.cMirrorReminder,
        cReceiveEmail = this.cReceiveEmail,
        cSpliceCutCompleteReminder = this.cSpliceCutCompleteReminder,
        cSpliceMultiplePieceReminder = this.cSpliceMultiplePieceReminder,
        cSpliceReminder = this.cSpliceReminder,
        cCuttingReminder = this.cCuttingReminder,
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
        cSubscriptionPlanEndDate = this.cSubscriptionPlanEndDate,
        cSubscriptionPlanStartDate = this.cSubscriptionPlanStartDate,
        cSubscriptionPlanPrice = this.cSubscriptionPlanPrice,
        cSubscriptionPlanId = this.cSubscriptionPlanId,
        cSubscriptionPlanName = this.cSubscriptionPlanName,
        cSubscriptionID = this.cSubscriptionID,
        cSubscriptionPlanCurrency = this.cSubscriptionPlanCurrency,
        cSubscriptionType = this.cVectorKey,
        cSubscriptionPlanBillingEndDate = this.cSubscriptionPlanBillingEndDate,
        cSubscriptionPlanBillingStartDate = this.cSubscriptionPlanBillingStartDate


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