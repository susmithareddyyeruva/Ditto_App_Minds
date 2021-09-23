package core.data.model

import com.google.gson.annotations.SerializedName


data class VersionResult(
    @SerializedName("version") val version : String?="",
    @SerializedName("critical_version") val critical_version : String?="",
    @SerializedName("force_version") val force_version : String?="",
    @SerializedName("body") val body : String?="",
    @SerializedName("confirm") val confirm : String?="",
    @SerializedName("confirm_link") val confirm_link : String?="",
    @SerializedName("cancel") val cancel : String?="",
    @SerializedName("version_update") val version_update : Boolean,
    @SerializedName("critical_update") val critical_update :Boolean,
    @SerializedName("force_update") val force_update : Boolean

)