package core.data.model

import com.google.gson.annotations.SerializedName

data class TokenResult(
    @SerializedName("access_token")
    val access_token: String,
    @SerializedName("expires_in")
    val expires_in: Int,
    @SerializedName("token_type")
    val token_type: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("error_description")
    val error_description: String,

)