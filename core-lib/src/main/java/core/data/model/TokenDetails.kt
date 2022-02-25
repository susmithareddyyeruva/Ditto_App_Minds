package core.data.model

import com.google.gson.annotations.SerializedName

data class TokenDetails(
    @SerializedName("access_token") val accessToken : String?="",
    @SerializedName("expires_in") val expiresIn : Int?=0,
    @SerializedName("token_type") val tokenType : String?="",
    @SerializedName("sk") val sk : Int?=0

)