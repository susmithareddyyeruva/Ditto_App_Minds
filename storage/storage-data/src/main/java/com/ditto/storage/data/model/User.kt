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
    @ColumnInfo(name = "c_saveCalibrationPhotos")
    @SerializedName("c_saveCalibrationPhotos")
    val cSaveCalibrationPhotos: Boolean?,
    @ColumnInfo(name = "c_InitialisationVector")
    @SerializedName("c_InitialisationVector")
    var cInitialisationVector: String?,
    @ColumnInfo(name = "c_vectorKey")
    @SerializedName("c_vectorKey")
    var cVectorKey: String?,

    @ColumnInfo(name = "c_subscriptionValid")
    @SerializedName("c_subscriptionValid")
    val cSubscriptionValid: Boolean?,
    @ColumnInfo(name = "c_subscriptionPlanEndDate")
    @SerializedName("c_subscriptionPlanEndDate")
    val cSubscriptionPlanEndDate: String?,
    @ColumnInfo(name = "c_subscriptionPlanStartDate")
    @SerializedName("c_subscriptionPlanStartDate")
    val cSubscriptionPlanStartDate: String?,
    @ColumnInfo(name = "c_subscriptionPlanPrice")
    @SerializedName("c_subscriptionPlanPrice")
    val cSubscriptionPlanPrice: String?,
    @ColumnInfo(name = "c_subscriptionPlanId")
    @SerializedName("c_subscriptionPlanId")
    val cSubscriptionPlanId: String?,
    @ColumnInfo(name = "c_subscriptionPlanName")
    @SerializedName("c_subscriptionPlanName")
    val cSubscriptionPlanName: String?,
    @ColumnInfo(name = "c_subscriptionID")
    @SerializedName("c_subscriptionID")
    val cSubscriptionID: String?,
    @ColumnInfo(name = "c_subscriptionPlanCurrency")
    @SerializedName("c_subscriptionPlanCurrency")
    val cSubscriptionPlanCurrency: String?,
    @ColumnInfo(name = "c_subscriptionType")
    @SerializedName("c_subscriptionType")
    val cSubscriptionType: String?,
    @ColumnInfo(name = "c_subscriptionPlanBillingEndDate")
    @SerializedName("c_subscriptionPlanBillingEndDate")
    val cSubscriptionPlanBillingEndDate: String?,
    @ColumnInfo(name = "c_subscriptionPlanBillingStartDate")
    @SerializedName("c_subscriptionPlanBillingStartDate")
    val cSubscriptionPlanBillingStartDate: String?

)