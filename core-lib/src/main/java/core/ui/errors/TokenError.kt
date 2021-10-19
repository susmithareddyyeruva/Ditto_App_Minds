package core.ui.errors


import com.google.gson.annotations.SerializedName

data class TokenError(
    @SerializedName("fault")
    var fault: Fault,
    @SerializedName("_v")
    var v: String
)