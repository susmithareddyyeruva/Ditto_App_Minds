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
import com.google.gson.Gson

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
        cSubscriptionType = this.cSubscriptionType,
        cSubscriptionPlanBillingEndDate = this.cSubscriptionPlanBillingEndDate,
        cSubscriptionPlanBillingStartDate = this.cSubscriptionPlanBillingStartDate,
        c_encryptionKey = this.c_encryptionKey


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

fun mapResponseToModel(responseTypeError: Boolean): LoginResultDomain {
    val gson = Gson()
    if(responseTypeError) {
        val result = gson.fromJson(dummyErrorLoginResponse, LoginResult::class.java)
        return result.toUserDomain()

    } else {
        val result = gson.fromJson(dummyLoginResponse, LoginResult::class.java)
        return result.toUserDomain()
    }

}

const val dummyLoginResponse = """
    {
  "_v": "19.1",
  "_type": "customer",
  "auth_type": "registered",
  "creation_date": "2021-10-12T15:06:12.000Z",
  "customer_id": "abcgkk33VRNlDOEkKNLQJWCE7y",
  "customer_no": "00005001",
  "email": "test@test.com",
  "enabled": true,
  "first_name": "TestFirstName",
  "gender": 0,
  "last_login_time": "2022-01-17T13:23:54.465Z",
  "last_modified": "2022-01-17T13:23:54.711Z",
  "last_name": "testLastName",
  "last_visit_time": "2022-01-17T13:23:54.465Z",
  "login": "test@test.com",
  "phone_home": "9999999999",
  "previous_login_time": "2022-01-17T09:13:51.786Z",
  "previous_visit_time": "2022-01-17T09:13:51.786Z",
  "c_addVideoSkip": false,
  "c_autoRenewal": true,
  "c_cuttingReminder": true,
  "c_doNotOnBoarding": false,
  "c_interestArt": false,
  "c_interestBridalSpecialOccasionProjects": false,
  "c_interestClassroomCraftsDecor": false,
  "c_interestFloral": false,
  "c_interestFoodCrafts": false,
  "c_interestHolidayPartyDecorating": false,
  "c_interestHomeDecor": false,
  "c_interestJewelry": false,
  "c_interestKidsCrafts": false,
  "c_interestKnittingCrochet": false,
  "c_interestPaperCrafts": false,
  "c_interestQuiltingSewingFabric": false,
  "c_isCostumeGuildEnrolled": false,
  "c_isFourHEnrolled": false,
  "c_isGirlScoutsEnrolled": false,
  "c_isJoannPlusCustomer": false,
  "c_isMilitaryEnrolled": false,
  "c_isTaxExempt": false,
  "c_isTeacherEnrolled": false,
  "c_learnBrowseSocialMedia": false,
  "c_learnLookOnline": false,
  "c_learnTakeAClass": false,
  "c_learnVisitJoannStore": false,
  "c_mirrorReminder": true,
  "c_myfavouritePatterns": [
    "5fb11253ff97472fb947c93bb939cc87",
    "1311ccd51de4476ea6e3729682042424",
    "acde3c8155f04ea79c89805c27ce55c5",
    "abcd",
    "ce3af646b7a24ba78a926b8aad8c79c8"
  ],
  "c_numberOfCutReminder": false,
  "c_receiveDirectMail": false,
  "c_receiveEmail": false,
  "c_receiveTextMessage": false,
  "c_registeredWithNarvar": false,
  "c_remainingBillingCycles": 0.0,
  "c_renewalBillingCycles": 1.0,
  "c_spliceCutCompleteReminder": true,
  "c_spliceMultiplePieceReminder": true,
  "c_spliceReminder": true,
  "c_subscriptionCancelDate": "1970-01-01T00:00:00.000Z",
  "c_subscriptionExpireDate": "1970-01-01T00:00:00.000Z",
  "c_subscriptionID": "q72bfpnuhq1b",
  "c_subscriptionPlanBillingEndDate": "2023-01-10T07:46:10.000Z",
  "c_subscriptionPlanBillingStartDate": "2022-01-10T07:46:10.000Z",
  "c_subscriptionPlanCode": "ditto4",
  "c_subscriptionPlanCurrency": "USD",
  "c_subscriptionPlanEndDate": "2023-01-10T07:46:10.000Z",
  "c_subscriptionPlanId": "pcl239nbaxu3",
  "c_subscriptionPlanName": "PDF Patterns Annual Plan",
  "c_subscriptionPlanPrice": 179.0,
  "c_subscriptionPlanStartDate": "2022-01-10T07:46:10.000Z",
  "c_subscriptionStatus": "active",
  "c_subscriptionType": "plan",
  "c_subscriptionValid": true,
  "c_taxExempt": false,
  "c_traceAppFolderData": "{\"shibi\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\",\"8c99f48dbca94a77ad534b6b560574a5\",\"e7e5023d5d544503ae2b87e1371e4980\",\"5f17862bbd6d44c3b7410ae798ae7e0f\",\"e7e5023d5d544503ae2b87e1371e4980\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\"],\"QA\":[\"\"],\"clien001\":[\"5fb11253ff97472fb947c93bb939cc87\",\"8c99f48dbca94a77ad534b6b560574a5\"],\"client002\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\"],\"client003\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\"],\"client004\":[\"8c99f48dbca94a77ad534b6b560574a5\"],\"client123\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\",\"d1ae6387258f45d190b8e8112bf6485c\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\"],\"My Patterns \":[\"\",\"7c8c1f21f2174d2080964640220263de\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"e7e5023d5d544503ae2b87e1371e4980\",\"e36e1b127c054a50950f2cb21fb47f42\"],\"LivinTest\":[\"5f17862bbd6d44c3b7410ae798ae7e0f\",\"e7e5023d5d544503ae2b87e1371e4980\"]}",
  "c_encryptionKey": "",
  "c_vectorKey": "",
  "c_InitialisationVector": ""
}
"""

const val dummyErrorLoginResponse = """
     {
    "_v": "19.1",
    "_type": "customer",
  "auth_type": "registered",
  "creation_date": "2021-10-12T15:06:12.000Z",
  "customer_id": "abcgkk33VRNlDOEkKNLQJWCE7y",
  "customer_no": "00005001",
  "email": "test@test.com",
  "enabled": true,
  "first_name": "TestFirstName",
  "gender": 0,
  "last_login_time": "2022-01-17T13:23:54.465Z",
  "last_modified": "2022-01-17T13:23:54.711Z",
  "last_name": "testLastName",
  "last_visit_time": "2022-01-17T13:23:54.465Z",
  "login": "test@test.com",
  "phone_home": "9999999999",
  "previous_login_time": "2022-01-17T09:13:51.786Z",
  "previous_visit_time": "2022-01-17T09:13:51.786Z",
  "c_addVideoSkip": false,
  "c_autoRenewal": true,
  "c_cuttingReminder": true,
  "c_doNotOnBoarding": false,
  "c_interestArt": false,
  "c_interestBridalSpecialOccasionProjects": false,
  "c_interestClassroomCraftsDecor": false,
  "c_interestFloral": false,
  "c_interestFoodCrafts": false,
  "c_interestHolidayPartyDecorating": false,
  "c_interestHomeDecor": false,
  "c_interestJewelry": false,
  "c_interestKidsCrafts": false,
  "c_interestKnittingCrochet": false,
  "c_interestPaperCrafts": false,
  "c_interestQuiltingSewingFabric": false,
  "c_isCostumeGuildEnrolled": false,
  "c_isFourHEnrolled": false,
  "c_isGirlScoutsEnrolled": false,
  "c_isJoannPlusCustomer": false,
  "c_isMilitaryEnrolled": false,
  "c_isTaxExempt": false,
  "c_isTeacherEnrolled": false,
  "c_learnBrowseSocialMedia": false,
  "c_learnLookOnline": false,
  "c_learnTakeAClass": false,
  "c_learnVisitJoannStore": false,
  "c_mirrorReminder": true,
  "c_myfavouritePatterns": [
    "5fb11253ff97472fb947c93bb939cc87",
    "1311ccd51de4476ea6e3729682042424",
    "acde3c8155f04ea79c89805c27ce55c5",
    "abcd",
    "ce3af646b7a24ba78a926b8aad8c79c8"
  ],
  "c_numberOfCutReminder": false,
  "c_receiveDirectMail": false,
  "c_receiveEmail": false,
  "c_receiveTextMessage": false,
  "c_registeredWithNarvar": false,
  "c_remainingBillingCycles": 0.0,
  "c_renewalBillingCycles": 1.0,
  "c_spliceCutCompleteReminder": true,
  "c_spliceMultiplePieceReminder": true,
  "c_spliceReminder": true,
  "c_subscriptionCancelDate": "1970-01-01T00:00:00.000Z",
  "c_subscriptionExpireDate": "1970-01-01T00:00:00.000Z",
  "c_subscriptionID": "q72bfpnuhq1b",
  "c_subscriptionPlanBillingEndDate": "2023-01-10T07:46:10.000Z",
  "c_subscriptionPlanBillingStartDate": "2022-01-10T07:46:10.000Z",
  "c_subscriptionPlanCode": "ditto4",
  "c_subscriptionPlanCurrency": "USD",
  "c_subscriptionPlanEndDate": "2023-01-10T07:46:10.000Z",
  "c_subscriptionPlanId": "pcl239nbaxu3",
  "c_subscriptionPlanName": "PDF Patterns Annual Plan",
  "c_subscriptionPlanPrice": 179.0,
  "c_subscriptionPlanStartDate": "2022-01-10T07:46:10.000Z",
  "c_subscriptionStatus": "active",
  "c_subscriptionType": "plan",
  "c_subscriptionValid": true,
  "c_taxExempt": false,
  "c_traceAppFolderData": "{\"shibi\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\",\"8c99f48dbca94a77ad534b6b560574a5\",\"e7e5023d5d544503ae2b87e1371e4980\",\"5f17862bbd6d44c3b7410ae798ae7e0f\",\"e7e5023d5d544503ae2b87e1371e4980\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\"],\"QA\":[\"\"],\"clien001\":[\"5fb11253ff97472fb947c93bb939cc87\",\"8c99f48dbca94a77ad534b6b560574a5\"],\"client002\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\"],\"client003\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\"],\"client004\":[\"8c99f48dbca94a77ad534b6b560574a5\"],\"client123\":[\"\",\"8c99f48dbca94a77ad534b6b560574a5\",\"d1ae6387258f45d190b8e8112bf6485c\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\"],\"My Patterns \":[\"\",\"7c8c1f21f2174d2080964640220263de\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"ce3af646b7a24ba78a926b8aad8c79c8\",\"e7e5023d5d544503ae2b87e1371e4980\",\"e36e1b127c054a50950f2cb21fb47f42\"],\"LivinTest\":[\"5f17862bbd6d44c3b7410ae798ae7e0f\",\"e7e5023d5d544503ae2b87e1371e4980\"]}",
  "c_encryptionKey": "",
  "c_vectorKey": "",
  "c_InitialisationVector": "",
    "fault": {
        "arguments": {"credentialType": "adfdaff"},
        "message": "error found",
        "type": "fake"
        }
    }
"""