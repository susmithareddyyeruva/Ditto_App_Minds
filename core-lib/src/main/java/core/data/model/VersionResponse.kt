package core.data.model

import com.google.gson.annotations.SerializedName

data class VersionResponse (
    @SerializedName("response") val response : VersionResult,
    @SerializedName("errors") val errors : Errors
)