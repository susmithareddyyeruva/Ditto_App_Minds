package com.ditto.login.data.model

import com.ditto.login.data.error.Fault
import com.google.gson.annotations.SerializedName

data class LoginResult(
    @SerializedName("auth_type")
    val authType: String,
    @SerializedName("creation_date")
    val creationDate: String,
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("customer_no")
    val customerNo: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_home")
    val phoneHome: String,
    @SerializedName("enabled")
    val enabled: Boolean,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("gender")
    val gender: Int,
    @SerializedName("last_login_time")
    val lastLoginTime: String,
    @SerializedName("last_modified")
    val lastModified: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("last_visit_time")
    val lastVisitTime: String,
    @SerializedName("login")
    val login: String,
    @SerializedName("previous_login_time")
    val previousLoginTime: String,
    @SerializedName("previous_visit_time")
    val previousVisitTime: String,
    @SerializedName("salutation")
    val salutation: String,
    @SerializedName("_type")
    val type: String,
    @SerializedName("_v")
    val v: String,
    @SerializedName("fault")
    val fault: Fault?,
    @SerializedName("c_mirrorReminder")
    val cMirrorReminder: Boolean,
    @SerializedName("c_receiveEmail")
    val cReceiveEmail: Boolean,
    @SerializedName("c_spliceCutCompleteReminder")
    val cSpliceCutCompleteReminder: Boolean,
    @SerializedName("c_spliceMultiplePieceReminder")
    val cSpliceMultiplePieceReminder: Boolean,
    @SerializedName("c_spliceReminder")
    val cSpliceReminder: Boolean,
    @SerializedName("c_cuttingReminder")
    val cCuttingReminder: Boolean,
    @SerializedName("c_interestArt")
    val cInterestArt: Boolean,
    @SerializedName("c_interestBridalSpecialOccasionProjects")
    val cInterestBridalSpecialOccasionProjects: Boolean,
    @SerializedName("c_interestClassroomCraftsDecor")
    val cInterestClassroomCraftsDecor: Boolean,
    @SerializedName("c_interestFloral")
    val cInterestFloral: Boolean,
    @SerializedName("c_interestFoodCrafts")
    val cInterestFoodCrafts: Boolean,
    @SerializedName("c_interestHolidayPartyDecorating")
    val cInterestHolidayPartyDecorating: Boolean,
    @SerializedName("c_interestHomeDecor")
    val cInterestHomeDecor: Boolean,
    @SerializedName("c_interestJewelry")
    val cInterestJewelry: Boolean,
    @SerializedName("c_interestKidsCrafts")
    val cInterestKidsCrafts: Boolean,
    @SerializedName("c_interestKnittingCrochet")
    val cInterestKnittingCrochet: Boolean,
    @SerializedName("c_interestPaperCrafts")
    val cInterestPaperCrafts: Boolean,
    @SerializedName("c_interestQuiltingSewingFabric")
    val cInterestQuiltingSewingFabric: Boolean,
    @SerializedName("c_isCostumeGuildEnrolled")
    val cIsCostumeGuildEnrolled: Boolean,
    @SerializedName("c_isFourHEnrolled")
    val cIsFourHEnrolled: Boolean,
    @SerializedName("c_isGirlScoutsEnrolled")
    val cIsGirlScoutsEnrolled: Boolean,
    @SerializedName("c_isJoannPlusCustomer")
    val cIsJoannPlusCustomer: Boolean,
    @SerializedName("c_isMilitaryEnrolled")
    val cIsMilitaryEnrolled: Boolean,
    @SerializedName("c_isTaxExempt")
    val cIsTaxExempt: Boolean,
    @SerializedName("c_isTeacherEnrolled")
    val cIsTeacherEnrolled: Boolean,
    @SerializedName("c_learnBrowseSocialMedia")
    val cLearnBrowseSocialMedia: Boolean,
    @SerializedName("c_learnLookOnline")
    val cLearnLookOnline: Boolean,
    @SerializedName("c_learnTakeAClass")
    val cLearnTakeAClass: Boolean,
    @SerializedName("c_learnVisitJoannStore")
    val cLearnVisitJoannStore: Boolean,
    @SerializedName("c_receiveDirectMail")
    val cReceiveDirectMail: Boolean,
    @SerializedName("c_receiveTextMessage")
    val cReceiveTextMessage: Boolean,
    @SerializedName("c_registeredWithNarvar")
    val cRegisteredWithNarvar: Boolean,
    @SerializedName("c_taxExempt")
    val cTaxExempt: Boolean,
    @SerializedName("c_InitialisationVector")
    val cInitialisationVector: String,
    @SerializedName("c_vectorKey")
    val cVectorKey: String,

    @SerializedName("c_subscriptionValid")
    val cSubscriptionValid: Boolean,
    @SerializedName("c_subscriptionPlanEndDate")
    val cSubscriptionPlanEndDate: String,
    @SerializedName("c_subscriptionPlanStartDate")
    val cSubscriptionPlanStartDate: String,
    @SerializedName("c_subscriptionPlanPrice")
    val cSubscriptionPlanPrice: String,
    @SerializedName("c_subscriptionPlanId")
    val cSubscriptionPlanId: String,
    @SerializedName("c_subscriptionPlanName")
    val cSubscriptionPlanName: String,
    @SerializedName("c_subscriptionID")
    val cSubscriptionID: String,
    @SerializedName("c_subscriptionPlanCurrency")
    val cSubscriptionPlanCurrency: String,
    @SerializedName("c_subscriptionType")
    val cSubscriptionType: String,
    @SerializedName("c_subscriptionPlanBillingEndDate")
    val cSubscriptionPlanBillingEndDate: String,
    @SerializedName("c_subscriptionPlanBillingStartDate")
    val cSubscriptionPlanBillingStartDate: String,
    @SerializedName("c_encryptionKey")
    val c_encryptionKey:String?

)