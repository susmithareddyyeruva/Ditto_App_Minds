package core.data.model

import com.google.gson.annotations.SerializedName


data class VersionResult(
    @SerializedName("version") val version : String?="",
    @SerializedName("critical_version") val criticalVersion : String?="",
    @SerializedName("force_version") val forceVersion : String?="",
    @SerializedName("title") val title : String?="",
    @SerializedName("body") val body : String?="",
    @SerializedName("confirm") val confirm : String?="",
    @SerializedName("confirm_link") val confirmLink : String?="",
    @SerializedName("cancel") val cancel : String?="",
    @SerializedName("version_update") val versionUpdate : Boolean,
    @SerializedName("critical_update") val criticalUpdate :Boolean,
    @SerializedName("force_update") val forceUpdate : Boolean

)