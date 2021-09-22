package core.data.model

import com.google.gson.annotations.SerializedName


data class VersionResult(
    @SerializedName("version") val version : String?="",
    @SerializedName("critical_version") val critical_version : String?="",
    @SerializedName("force_version") val force_version : String?="",
    @SerializedName("body") val body : String?="",
    @SerializedName("confirm") val confirm : String?="",
    @SerializedName("confirm_link") val confirm_link : String?="",
    @SerializedName("cancel") val cancel : String?=""

)