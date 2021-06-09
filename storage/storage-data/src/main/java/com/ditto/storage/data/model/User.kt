package com.ditto.storage.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_data")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Int = 0,
    @ColumnInfo(name = "userName")
    @SerializedName("userName")
    var userName: String?,
    @ColumnInfo(name = "isLoggedIn")
    @SerializedName("isLoggedIn")
    var isLoggedIn: Boolean?,
    @ColumnInfo(name = "dndOnboarding")
    @SerializedName("dndOnboarding")
    var dndOnboarding: Boolean?,
    @ColumnInfo(name = "bleDialogVisible")
    @SerializedName("bleDialogVisible")
    var bleDialogVisible: Boolean?,
    @ColumnInfo(name = "wifiDialogVisible")
    @SerializedName("wifiDialogVisible")
    var wifiDialogVisible: Boolean?,
    @ColumnInfo(name = "c_mirrorReminder")
    @SerializedName("c_mirrorReminder")
    val cMirrorReminder: Boolean?,
    @ColumnInfo(name = "c_receiveEmail")
    @SerializedName("c_receiveEmail")
    val cReceiveEmail: Boolean?,
    @ColumnInfo(name = "c_spliceCutCompleteReminder")
    @SerializedName("c_spliceCutCompleteReminder")
    val cSpliceCutCompleteReminder: Boolean?,
    @ColumnInfo(name = "c_spliceMultiplePieceReminder")
    @SerializedName("c_spliceMultiplePieceReminder")
    val cSpliceMultiplePieceReminder: Boolean?,
    @ColumnInfo(name = "c_spliceReminder")
    @SerializedName("c_spliceReminder")
    val cSpliceReminder: Boolean?,
    @ColumnInfo(name = "c_cuttingReminder")
    @SerializedName("c_cuttingReminder")
    val cCuttingReminder: Boolean?,
    @ColumnInfo(name = "c_interestArt")
    @SerializedName("c_interestArt")
    val cInterestArt: Boolean?,
    @ColumnInfo(name = "c_interestBridalSpecialOccasionProjects")
    @SerializedName("c_interestBridalSpecialOccasionProjects")
    val cInterestBridalSpecialOccasionProjects: Boolean?,
    @ColumnInfo(name = "c_interestClassroomCraftsDecor")
    @SerializedName("c_interestClassroomCraftsDecor")
    val cInterestClassroomCraftsDecor: Boolean?,
    @ColumnInfo(name = "c_interestFloral")
    @SerializedName("c_interestFloral")
    val cInterestFloral: Boolean?,
    @ColumnInfo(name = "c_interestFoodCrafts")
    @SerializedName("c_interestFoodCrafts")
    val cInterestFoodCrafts: Boolean?,
    @ColumnInfo(name = "c_interestHolidayPartyDecorating")
    @SerializedName("c_interestHolidayPartyDecorating")
    val cInterestHolidayPartyDecorating: Boolean?,
    @ColumnInfo(name = "c_interestHomeDecor")
    @SerializedName("c_interestHomeDecor")
    val cInterestHomeDecor: Boolean?,
    @ColumnInfo(name = "c_interestJewelry")
    @SerializedName("c_interestJewelry")
    val cInterestJewelry: Boolean?,
    @ColumnInfo(name = "c_interestKidsCrafts")
    @SerializedName("c_interestKidsCrafts")
    val cInterestKidsCrafts: Boolean?,
    @ColumnInfo(name = "c_interestKnittingCrochet")
    @SerializedName("c_interestKnittingCrochet")
    val cInterestKnittingCrochet: Boolean?,
    @ColumnInfo(name = "c_interestPaperCrafts")
    @SerializedName("c_interestPaperCrafts")
    val cInterestPaperCrafts: Boolean?,
    @ColumnInfo(name = "c_interestQuiltingSewingFabric")
    @SerializedName("c_interestQuiltingSewingFabric")
    val cInterestQuiltingSewingFabric: Boolean?,
    @ColumnInfo(name = "c_isCostumeGuildEnrolled")
    @SerializedName("c_isCostumeGuildEnrolled")
    val cIsCostumeGuildEnrolled: Boolean?,
    @ColumnInfo(name = "c_isFourHEnrolled")
    @SerializedName("c_isFourHEnrolled")
    val cIsFourHEnrolled: Boolean?,
    @ColumnInfo(name = "c_isGirlScoutsEnrolled")
    @SerializedName("c_isGirlScoutsEnrolled")
    val cIsGirlScoutsEnrolled: Boolean?,
    @ColumnInfo(name = "c_isJoannPlusCustomer")
    @SerializedName("c_isJoannPlusCustomer")
    val cIsJoannPlusCustomer: Boolean?,
    @ColumnInfo(name = "c_isMilitaryEnrolled")
    @SerializedName("c_isMilitaryEnrolled")
    val cIsMilitaryEnrolled: Boolean?,
    @ColumnInfo(name = "c_isTaxExempt")
    @SerializedName("c_isTaxExempt")
    val cIsTaxExempt: Boolean?,
    @ColumnInfo(name = "c_isTeacherEnrolled")
    @SerializedName("c_isTeacherEnrolled")
    val cIsTeacherEnrolled: Boolean?,
    @ColumnInfo(name = "c_learnBrowseSocialMedia")
    @SerializedName("c_learnBrowseSocialMedia")
    val cLearnBrowseSocialMedia: Boolean?,
    @ColumnInfo(name = "c_learnLookOnline")
    @SerializedName("c_learnLookOnline")
    val cLearnLookOnline: Boolean?,
    @ColumnInfo(name = "c_learnTakeAClass")
    @SerializedName("c_learnTakeAClass")
    val cLearnTakeAClass: Boolean?,
    @ColumnInfo(name = "c_learnVisitJoannStore")
    @SerializedName("c_learnVisitJoannStore")
    val cLearnVisitJoannStore: Boolean?,
    @ColumnInfo(name = "c_receiveDirectMail")
    @SerializedName("c_receiveDirectMail")
    val cReceiveDirectMail: Boolean?,
    @ColumnInfo(name = "c_receiveTextMessage")
    @SerializedName("c_receiveTextMessage")
    val cReceiveTextMessage: Boolean?,
    @ColumnInfo(name = "c_registeredWithNarvar")
    @SerializedName("c_registeredWithNarvar")
    val cRegisteredWithNarvar: Boolean?,
    @ColumnInfo(name = "c_taxExempt")
    @SerializedName("c_taxExempt")
    val cTaxExempt: Boolean?,
    @ColumnInfo(name = "c_InitialisationVector")
    @SerializedName("c_InitialisationVector")
    var cInitialisationVector: String?,
    @ColumnInfo(name = "c_vectorKey")
    @SerializedName("c_vectorKey")
    var cVectorKey: String?

)